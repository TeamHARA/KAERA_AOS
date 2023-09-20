package com.hara.kaera.data.datasource.remote

import com.hara.kaera.data.dto.login.JWTRefreshReqDTO
import com.hara.kaera.data.dto.login.JWTRefreshResDTO
import com.hara.kaera.data.dto.login.KaKaoLoginReqDTO
import com.hara.kaera.data.dto.login.KaKaoLoginResDTO
import kotlinx.coroutines.flow.Flow

interface LoginDataSource {
    fun getKakaoLoginJWT(accessToken: KaKaoLoginReqDTO): Flow<KaKaoLoginResDTO>

    fun getAccessToken(tokens : JWTRefreshReqDTO): Flow<JWTRefreshResDTO>
}