package com.hara.kaera.feature.util

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KaKaoLoginClient @Inject constructor(
    @ActivityContext private val context: Context,
    private val userApiClient: UserApiClient
) {

    suspend fun login(): Result<OAuthToken> = runCatching {
        if (userApiClient.isKakaoTalkLoginAvailable(context)) { // 카카오톡이 설치되어있는가?
            try {
                UserApiClient.loginWithKakaoTalk(context)
            } catch (error: Throwable) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    // 사용자가 뒤로가기 선택으로 의도적 취소
                    throw error
                } else {
                    UserApiClient.loginWithKakaoAccount(context)
                }
            }
        } else { // 카카오계정(웹)으로 로그인
            UserApiClient.loginWithKakaoAccount(context)
        }
    }

    // 카카오톡으로 로그인 시도
    private suspend fun UserApiClient.Companion.loginWithKakaoTalk(context: Context): OAuthToken =
        suspendCoroutine { continuation ->
            instance.loginWithKakaoTalk(context) { token, error ->
                continuation.resumeTokenOrException(token, error)
            }
        }

    // 카카오 계정으로 로그인 시도
    private suspend fun UserApiClient.Companion.loginWithKakaoAccount(context: Context): OAuthToken =
        suspendCoroutine { continuation ->
            instance.loginWithKakaoAccount(context) { token, error ->
                continuation.resumeTokenOrException(token, error)
            }
        }

    suspend fun logout():Result<Unit> = runCatching{
        try {
            UserApiClient.logOut()
        }catch (error:Throwable){
            throw error
        }
    }

    // 로그아웃
    private suspend fun UserApiClient.Companion.logOut() {
        suspendCoroutine { continuation ->
            instance.logout { error ->
                continuation.resumeLogout(error)
            }
        }
    }

    private fun Continuation<OAuthToken>.resumeTokenOrException(
        token: OAuthToken?,
        error: Throwable?
    ) {
        if (error != null) {
            resumeWithException(error)
        } else if (token != null) {
            resume(token)
        } else {
            resumeWithException(RuntimeException("토큰 접근 에러"))
        }
    }

    private fun Continuation<Unit>.resumeLogout(
        error: Throwable?
    ) {
        if (error != null) {
            resumeWithException(error)
        }else{
            resume(Unit)
        }
    }
}
