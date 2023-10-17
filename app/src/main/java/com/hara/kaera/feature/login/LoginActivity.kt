package com.hara.kaera.feature.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hara.kaera.R
import com.hara.kaera.databinding.ActivityLoginBinding
import com.hara.kaera.feature.MainActivity
import com.hara.kaera.feature.base.BindingActivity
import com.hara.kaera.feature.util.KaKaoLoginClient
import com.hara.kaera.feature.util.TokenState
import com.hara.kaera.feature.util.UiState
import com.hara.kaera.feature.util.makeToast
import com.hara.kaera.feature.util.onSingleClick
import com.hara.kaera.feature.util.visible
import com.kakao.sdk.auth.AuthApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BindingActivity<ActivityLoginBinding>(R.layout.activity_login) {

    private val loginViewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var kaKaoLoginClient: KaKaoLoginClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectFlow()
        binding.btnKakaoLogin.onSingleClick(300) { // 최초로그인 로직을 탐
            kakaoLogin()
        }

    }
    private fun collectFlow(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    loginViewModel.kakaoRemoteJWT.collect {
                        render(it)
                    }
                }
                launch {
                    loginViewModel.tokenState.collect {
                        tokenCheck(it)
                    }
                }
            }
        }
    }

    private fun kakaoLogin() { // 최초 혹은 이후 카카오로그인 과정
        lifecycleScope.launch {
            kotlin.runCatching {
                kaKaoLoginClient.login()
            }.onSuccess {
                // OAuthToken 뜯어서 서버에 리퀘스트바디로 전달
                // DataStore에 저장
                if (it.isSuccess) {
                    it.onSuccess { oAuthToken ->
                        loginViewModel.callKakaoLoginJWT(oAuthToken = oAuthToken)
                    }
                }
            }.onFailure {
                //에러
                binding.root.makeToast(it.toString())
                throw it
            }
        }
    }

    /*
    이 함수 자체가 카카오로그인을 한번 한 상태에서도 이루어지므로 나중에 splashActivity에서
    처리해도 되지 않을까 생각이 듭니다. 우선은 테스트용으로 여기에서 실행
     */
    private fun tokenCheck(tokenState: TokenState<String>) {
        Timber.e(tokenState.toString())
        when (tokenState) {
            is TokenState.Init -> Unit
            is TokenState.Empty -> {
                //데이터스토어에 토큰이 비어있는 상태
                //TODO 스플래시에서 이루어진다면 카카오로그인을 위해서 로그인 액티비티로 이동
                binding.root.makeToast("로그인 먼저 진행해주세요")
            }

            is TokenState.Exist -> {
                // TODO 이 상태 진입은 미리 이전 토큰이 저장되어있는 상태이므로
                // 토큰 재발급 호출 후 그 결과에 따라 데이터 스토어에 액세스토큰 갱신
                //loginViewModel.callUpdatedAccessToken()
                // 이때 만료상태라면 최초로그인 과정으로 진입
            }

            is TokenState.Valid -> {
                finishAffinity()
                startActivity(Intent(this, MainActivity::class.java))
            }
            else -> Unit
        }
    }

    private fun render(uiState: UiState<Unit>) {
        when (uiState) {
            is UiState.Init -> Unit
            is UiState.Loading -> {
                binding.loadingBar.visible(true)
            }

            is UiState.Success -> {
                binding.loadingBar.visible(false)
            }

            is UiState.Error -> {
                binding.root.makeToast(uiState.error)
            }

            is UiState.Empty -> {
                Unit
            }
        }
    }

}