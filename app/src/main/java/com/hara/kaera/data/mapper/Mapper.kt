package com.hara.kaera.data.mapper

import com.hara.kaera.data.dto.TemplateDetailDTO
import com.hara.kaera.data.dto.TemplateTypeDTO
import com.hara.kaera.domain.entity.TemplateDetailEntity
import com.hara.kaera.domain.entity.TemplateTypesEntity

/*
Mapper는 다음과 같이 DTO타입을 Entity형태로 즉. 실제로 사용할 데이터만 담아서
가공하는 역할 다음과 같이 status에 따라서 Entity의 가공을 다르게 해주고 있다.
물론 이형태 보다는 datasource에서 status로 판단해주는게 더 좋지만 그건 조금 더 공부해온 다음해
적용하고 핵심은 데이터를 DTO그대로 쓰는게 아니고 Entity라는 형태로 가공해서 써준다는 것!
 */

object Mapper {
    fun mapperToTemplateType(dto: TemplateTypeDTO): TemplateTypesEntity {
        if (dto.status in 400..499) { // 에러이므로 아무것도 넣지 않고 erroMessage에만 담아준다.
            return TemplateTypesEntity(
                errorMessage = "서버 상태가 불안정합니다. 잠시후 다시 시도해주세요",
                templateTypeList = null
            )
        } else if (dto.status in 500..599) {
            return TemplateTypesEntity(
                errorMessage = "네트워크상태가 불안정합니다.",
                templateTypeList = null
            )
        } else {
            val templateTypeList = mutableListOf<TemplateTypesEntity.Template>()
            dto.data.toList().forEach {
                templateTypeList.add(
                    TemplateTypesEntity.Template(
                        hasUsed = it.hasUsed,
                        info = it.info,
                        shortInfo = it.shortInfo,
                        templateId = it.templateId,
                        title = it.title
                    )
                )
            }
            return TemplateTypesEntity(
                errorMessage = null,
                templateTypeList = templateTypeList
            )
        }
    }

    fun mapperToTemplateDetail(dto: TemplateDetailDTO) : TemplateDetailEntity {
        if (dto.status in 400..499) { // 에러이므로 아무것도 넣지 않고 erroMessage에만 담아준다.
            return TemplateDetailEntity(
                errorMessage = "서버 상태가 불안정합니다. 잠시후 다시 시도해주세요",
                templateDetailInfo = null
            )
        } else if (dto.status in 500..599) {
            return TemplateDetailEntity(
                errorMessage = "네트워크상태가 불안정합니다.",
                templateDetailInfo = null
            )
        } else {
            var templateDetailInfo : TemplateDetailEntity.TemplateDetailInfo
            dto.data.let {
                templateDetailInfo = TemplateDetailEntity.TemplateDetailInfo(
                    it.guideline,
                    it.hints,
                    it.questions,
                    it.title
                )
            }
            return TemplateDetailEntity(
                errorMessage = null,
                templateDetailInfo = templateDetailInfo
            )
        }
    }


}