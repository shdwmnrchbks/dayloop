package com.shadowmonarchbooks.dayloop.pack.schema

import kotlinx.serialization.Serializable

/** Pack-authored request catalog whose completion is tied to exact walkthrough tasks. */
@Serializable
data class MementosRequestsFile(
    val requests: List<MementosRequestDefinition> = emptyList(),
    val events: List<AchievementEventAnchor> = emptyList(),
)

@Serializable
data class MementosRequestDefinition(
    val id: String,
    val title: String,
    /** Route date on which the guide obtains or identifies the request. */
    val receivedOn: String,
    /** Route date on which the guide defeats or resolves the target. */
    val expectedBy: String,
    val target: String? = null,
    val location: String? = null,
    val reward: String? = null,
    /** Exactly one semantic event representing actual request completion. */
    val completionEvent: String,
)
