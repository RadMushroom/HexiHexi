package com.hexihexi.hexihexi.activities

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hexihexi.hexihexi.HexiApp
import com.hexihexi.hexihexi.R
import com.hexihexi.hexihexi.Utils
import com.hexihexi.hexihexi.bluetooth.BluetoothServiceCallback
import com.hexihexi.hexihexi.bluetooth.Characteristic
import com.hexihexi.hexihexi.bluetooth.Characteristic.HEART_RATE
import com.hexihexi.hexihexi.bluetooth.HexiBluetoothService
import com.hexihexi.hexihexi.model.DeviceDetail
import com.hexihexi.hexihexi.view.BaseActivity
import com.hexihexi.hexihexi.view.DeviceDetailAdapter
import com.hexihexi.hexihexi.view.OnItemPositionClickListener
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_device_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DeviceDetailsActivity : BaseActivity(), BluetoothServiceCallback, OnItemPositionClickListener {

    @Inject
    internal lateinit var bluetoothService: HexiBluetoothService

    private lateinit var device: BluetoothDevice
    private val deviceDetailAdapter = DeviceDetailAdapter(this)
    private var auth = FirebaseAuth.getInstance()
    private var database = FirebaseDatabase.getInstance().reference
    private val heartRateSubject = PublishSubject.create<Float>()
    private val accelerationSubject = PublishSubject.create<List<Float>>()
    private val gyroSubject = PublishSubject.create<List<Float>>()
    private var disposable: Disposable? = null
    private var disposableTriple: Disposable? = null
    private var accData: List<Float> = listOf()
    private var gyroData: List<Float> = listOf()
    private val dateFormat = SimpleDateFormat("hh:mm:ss dd-MM-yyyy", Locale.getDefault())

    companion object {
        private const val EXTRA_DEVICE = "KEY_DEVICE"
        fun getStartIntent(context: Context, it: BluetoothDevice): Intent {
            return Intent(context, DeviceDetailsActivity::class.java).apply {
                putExtra(EXTRA_DEVICE, it)
            }
        }
    }

    override fun getContentView(): Int = R.layout.activity_device_details

    private var buffering = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HexiApp.hexiComponent().inject(this)
        device = intent.extras.getParcelable(EXTRA_DEVICE)
        userInfo.text = "Current user: ${auth.currentUser?.email}"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = device.name
        deviceOptionsRecyclerView.adapter = deviceDetailAdapter
        bluetoothService.setUpWithDevice(device, this)
        disposable = heartRateSubject.throttleLast(5, TimeUnit.SECONDS, Schedulers.io())
                .buffer(3)
                .subscribe({
                    val message = when {
                        it.all { it == 0.0f } -> "Heart rate is zero!"
                        it.all { it > 0.0f && it < 50 } -> "Heart rate is critically low!"
                        it.all { it > 100.0f } -> "Heart rate is critically high!"
                        else -> "Something went wrong!"
                    }
                    database.child("users")
                            .child(auth.currentUser?.uid)
                            .child("data")
                            .child("heart_rate")
                            .child(dateFormat.format(Date())).setValue(message)
                }, { Log.e("Database_error", "Error while writing to database: ${it.message}") })
        Observable.zip(accelerationSubject, gyroSubject, BiFunction { f1: List<Float>, f2: List<Float> -> Pair(f1, f2) })
                .filter{
                    if (!buffering){
                        buffering = detectFaintFirstStep(it.first, it.second)
                    }
                    return@filter buffering
                }
                .buffer(5)
                .map {
                    val first = mutableListOf<List<Float>>()
                    val second = mutableListOf<List<Float>>()
                    it.forEach {
                        first.add(it.first)
                        second.add(it.second)
                    }
                    return@map Pair(first, second)
                }
                .subscribe({
                    val faintDetected = detectFaintSecondStep(it.first, it.second)
                    if (faintDetected){
                        database.child("users")
                                .child(auth.currentUser?.uid)
                                .child("data")
                                .child("faints")
                                .child(dateFormat.format(Date())).setValue("User's faint detected!")
                    }
                }, {Log.e("Database_error", "Error while writing to database: ${it.message}")})
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onConnectionStateChanged(isConnected: Boolean) {

    }

    override fun onNewDataAvailable(characteristic: Characteristic, value: String) {
        Log.i("RECEIVED: ", " ${characteristic.name} $value")
        when (characteristic) {
            Characteristic.ACCELERATION ->
                accData = Utils.parseTriple(value)
//                heartRateSubject.onNext(BTDataSnapshot("acceleration", Pair(dateFormat.format(Date()),Utils.parseTriple(value))))
            Characteristic.GYRO ->
                gyroData = Utils.parseTriple(value)
//                heartRateSubject.onNext(BTDataSnapshot("gyro", Pair(dateFormat.format(Date()),Utils.parseTriple(value))))
            HEART_RATE -> {
                heartRateSubject.onNext(Utils.parseValue(value))
                Log.i("HEART_RATE", "Heart rate missing!!!")
            }
        }
        runOnUiThread({
            deviceDetailAdapter.handleDetail(DeviceDetail(characteristic, value))
        })
    }

    override fun onItemClicked(position: Int) {
        deviceDetailAdapter.getItem(position)?.let {
            startActivity(ChartActivity.getStartIntent(this, device, it.type))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchUsers -> {
                startActivity(Intent(this, UsersListActivity::class.java))
            }
            R.id.log_out -> {
                auth.currentUser?.let {
                    database.child("users").child(it.uid).child("token").setValue("")
                }
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
                auth.signOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        listOfNotNull(disposable, disposableTriple)
                .filter { !it.isDisposed }
                .forEach { it.dispose() }
    }

    private fun detectFaintFirstStep(accData: List<Float>, gyroData: List<Float>): Boolean {
        val accRMS = Math.sqrt(((accData[0] * accData[0]).toDouble()) + ((accData[1] * accData[1]).toDouble())
                + ((accData[2] * accData[2]).toDouble())).toFloat()
        return accRMS <= 0.33
    }

    private fun detectFaintSecondStep(accData: List<List<Float>>, gyroData: List<List<Float>>): Boolean {
        var accRMS = 0f
        var gyroRMS = 0f
        (0 until accData.size).forEach{
            accRMS += Math.sqrt(((accData[it][0] * accData[it][0]).toDouble()) + ((accData[it][1] * accData[it][1]).toDouble())
                    + ((accData[it][2] * accData[it][2]).toDouble())).toFloat()
        }
        (0 until accData.size).forEach {
            gyroRMS += Math.sqrt(((gyroData[it][0] * gyroData[it][0]).toDouble()) + ((gyroData[it][1] * gyroData[it][1]).toDouble())
                    + ((gyroData[it][2] * gyroData[it][2]).toDouble())).toFloat()
        }
        return accRMS >= 2.4 && gyroRMS >= 240
    }
}
