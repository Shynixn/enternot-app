package at.jku.enternot.entity

class Response<C>(val statusCode: Int, val content: C? = null)