package com.capstone.attirely

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.capstone.attirely.helper.ModelDownloadWorker
import com.capstone.attirely.ui.theme.AttirelyTheme
import com.capstone.attirely.viewmodel.LoginViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    private val oneTapSignInLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val credential = result.data?.let { Identity.getSignInClient(this).getSignInCredentialFromIntent(it) }
            credential?.googleIdToken?.let { loginViewModel.handleSignInResult(it) }
        } else {
            Log.e("OneTapSignIn", "One-tap sign-in failed")
            // You can handle fallback here if necessary
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        // Schedule the model download worker
        val downloadWorkRequest = OneTimeWorkRequestBuilder<ModelDownloadWorker>().build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            "ModelDownloadWork",
            ExistingWorkPolicy.KEEP,
            downloadWorkRequest
        )

        setContent {
            AttirelyTheme {
                val firebaseAuthResult by loginViewModel.firebaseAuthResult.observeAsState()
                val currentUser = FirebaseAuth.getInstance().currentUser

                LaunchedEffect(currentUser, firebaseAuthResult) {
                    if (currentUser != null) {
                        loginViewModel.updateLoginState(true)
                    } else {
                        loginViewModel.updateLoginState(false)
                    }
                }

                val isLoggedIn by loginViewModel.isLoggedIn.observeAsState(initial = false)

                Crossfade(targetState = isLoggedIn) { loggedIn ->
                    if (loggedIn) {
                        MainScreen(onGoogleSignInClick = { initiateOneTapSignIn() })
                    } else {
                        WelcomePage(onGoogleSignInClick = { initiateOneTapSignIn() })
                    }
                }
            }
        }
    }

    private fun initiateOneTapSignIn() {
        if (checkGooglePlayServices()) {
            val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()

            loginViewModel.getOneTapClient().beginSignIn(signInRequest)
                .addOnSuccessListener { result ->
                    oneTapSignInLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                }
                .addOnFailureListener { e ->
                    Log.e("OneTapSignInError", "Error: ${e.message}")
                    // Handle the error or provide a fallback
                }
        } else {
            // Handle the case where Google Play Services is not available or up-to-date
            Log.e("GooglePlayServices", "Google Play Services is not available or up-to-date")
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 2404)?.show()
            }
            return false
        }
        return true
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WelcomePage(onGoogleSignInClick: () -> Unit) {
    val images = listOf(
        R.drawable.carousel1,
        R.drawable.carousel2,
        R.drawable.carousel3
    )

    val texts = listOf(
        R.string.carosel1,
        R.string.carosel2,
        R.string.carosel3
    )

    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(bottomStart = 180.dp, bottomEnd = 180.dp))
                .background(color = colorResource(id = R.color.secondary))
        ) { page ->
            Image(
                modifier = Modifier
                    .fillMaxSize(0.8f),
                painter = painterResource(id = images[page]),
                contentDescription = "Carousel Image ${page + 1}"
            )
        }

        Text(
            text = stringResource(id = texts[pagerState.currentPage]),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 30.dp, end = 30.dp),
        )

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp),
            activeColor = colorResource(id = R.color.primary),
            inactiveColor = colorResource(id = R.color.secondary),
            indicatorShape = CircleShape,
            indicatorWidth = 16.dp,
            indicatorHeight = 8.dp,
            spacing = 4.dp
        )

        Box(
            modifier = Modifier
                .height(230.dp)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
                .background(color = colorResource(id = R.color.primary))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { onGoogleSignInClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.white),
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .height(50.dp)
                                .width(50.dp),
                            painter = painterResource(id = R.drawable.logo_google),
                            contentDescription = "Google Logo"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(id = R.string.signIn),
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }
                }
                Text(
                    text = stringResource(id = R.string.tagLine),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewWelcomePage() {
    AttirelyTheme {
        WelcomePage(
            onGoogleSignInClick = {}
        )
    }
}