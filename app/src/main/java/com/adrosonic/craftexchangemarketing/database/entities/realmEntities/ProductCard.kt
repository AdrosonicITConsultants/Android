package com.adrosonic.craftexchangemarketing.database.entities.realmEntities

class ProductCard (
    var id : Long ?=0,
    var productId : Long?=0,
    var productTitle : String ?="",
    var productDescription : String ?="",
    var statusId : Long ?=0,
    var isWishlisted : Long ?=0
    )