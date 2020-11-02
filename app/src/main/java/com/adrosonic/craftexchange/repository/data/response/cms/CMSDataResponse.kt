package com.adrosonic.craftexchange.repository.data.response.cms

typealias CMSDataResponse = ArrayList<CMSDataResponseElement>

data class CMSDataResponseElement (
    val id: Long,
    val date: String,
    val dateGmt: String,
    val guid: GUID,
    val modified: String,
    val modifiedGmt: String,
    val slug: String,
    val status: String,
    val type: String,
    val link: String,
    val title: GUID,
    val content: Content,
    val featuredMedia: Long,
    val menuOrder: Long,
    val template: String,
    val meta: List<Any?>,
    val acf: Acf,
    val links: Links,
    val excerpt: Content,
    val author: Long,
    val parent: Long,
    val commentStatus: String,
    val pingStatus: String
)

data class Acf (
    val artisan_demo_video: String,
    val buyer_demo_video: String,
    val cluster_id: String,
    val header: String,
    val category_id: String,
    val image: String,
    val description: String,

//pages
    val background_image: String,
    val page_id: String,
    val title: String? = null,
    val paragraph: String? = null,
    val card_header: String? = null,
    val card_title: String? = null,
    val card_para: String? = null,
    val artisan_background: String? = null,
    val antaran_background: String? = null,
    val quoteMessage: String? = null,
    val artisanPara: String? = null,
    val artisanSubPara: String? = null,
    val antaranPara: String? = null,
    val antaranSubPara: String? = null,
    val card_background_1: String? = null,
    val card_background_2: String? = null,
    val cardBase: String? = null,
    val artisan_background_extended: String? = null,
    val antaran_background_extended: String? = null
)

data class GUID (
    val rendered: String
)

data class Links (
    val self: List<About>,
    val collection: List<About>,
    val about: List<About>,
    val wpAttachment: List<About>,
    val curies: List<Cury>,

    //pages
    val author: List<Author>,
    val replies: List<Author>,
    val versionHistory: List<VersionHistory>,
    val predecessorVersion: List<PredecessorVersion>
)

data class PredecessorVersion (
    val id: Long,
    val href: String
)

data class VersionHistory (
    val count: Long,
    val href: String
)

data class Author (
    val embeddable: Boolean,
    val href: String
)

data class About (
    val href: String
)

data class Content (
    val rendered: String,
    val protected: Boolean
)

data class Cury (
    val name: String,
    val href: String,
    val templated: Boolean
)