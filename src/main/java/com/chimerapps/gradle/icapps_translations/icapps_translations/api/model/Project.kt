/*
 * Copyright 2017-2022 - Chimerapps BV
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.chimerapps.gradle.icapps_translations.icapps_translations.api.model

import com.squareup.moshi.Json

/**
 * @author Nicola Verbeeck
 * @date 04/09/2017-2022.
 */

data class Language(
    @Json(name = "short_name") val languageCode: String?,
    @Json(name = "abbreviation") val alternativeLanguageCode: String?,
)
