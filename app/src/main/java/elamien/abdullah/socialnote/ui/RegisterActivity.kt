package elamien.abdullah.socialnote.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import elamien.abdullah.socialnote.R
import elamien.abdullah.socialnote.databinding.ActivityRegisterBinding
import elamien.abdullah.socialnote.eventbus.AuthenticationEvent
import elamien.abdullah.socialnote.utils.Constants
import elamien.abdullah.socialnote.viewmodel.AuthenticationViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.koin.android.ext.android.inject

class RegisterActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityRegisterBinding
    private var mGoogleSignInClient : GoogleSignInClient? = null
    private val mViewModel : AuthenticationViewModel by inject()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setupFullScreen()
        mBinding = DataBindingUtil.setContentView(this@RegisterActivity, R.layout.activity_register)
        mBinding.handlers = this
        registerEventBus()

    }

    private fun registerEventBus() {
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        unregisterEventBus()
    }

    private fun unregisterEventBus() {
        EventBus.getDefault().unregister(this)
    }

    private fun setupFullScreen() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun onGoogleClick(view : View) {
        mGoogleSignInClient = GoogleSignIn.getClient(this, getSignInOptions()!!)
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    private fun getSignInOptions() : GoogleSignInOptions? {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_api_key))
            .requestEmail()
            .build()

    }

    override fun onActivityResult(requestCode : Int, resultCode : Int, data : Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            mViewModel.registerGoogleUser(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    @Subscribe
    fun onEvent(event : AuthenticationEvent) {
        if (event.authenticationEventMessage == Constants.AUTH_EVENT_SUCCESS) {
            startHomeActivity()
        } else if (event.authenticationEventMessage == Constants.AUTH_EVENT_FAIL) {
            Toast.makeText(this@RegisterActivity, getString(R.string.auth_failed_msg), Toast.LENGTH_LONG).show()
        }
    }

    private fun startHomeActivity() {
        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 1
    }
}