package com.example.schemas

import com.example.events.UserEvent
import com.example.events.OrderEvent
import org.apache.avro.Schema
import org.apache.avro.reflect.ReflectData
import java.io.File

fun main() {
    val outDir = File("src/main/avro")
    outDir.mkdirs()

    // UserEvent 스키마 생성
    val userEventSchema = ReflectData.get().getSchema(UserEvent::class.java)
    File(outDir, "user-event.avsc").writeText(userEventSchema.toString(true))
    println("user-event.avsc 생성 완료")

    // OrderEvent 스키마 생성
    val orderEventSchema = ReflectData.get().getSchema(OrderEvent::class.java)
    File(outDir, "order-event.avsc").writeText(orderEventSchema.toString(true))
    println("order-event.avsc 생성 완료")
}
