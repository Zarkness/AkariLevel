package com.github.cpjinan.plugin.playerlevel.util

import taboolib.common.platform.function.adaptCommandSender
import taboolib.module.kether.*
import java.util.concurrent.CompletableFuture

/**
 * Vulpecula
 * top.lanscarlos.vulpecula.utils
 *
 * @author Lanscarlos
 * @since 2022-08-19 16:38
 */

object KetherUtil {
    fun String.toKetherScript(namespace: List<String> = emptyList()): Script {
        return if (namespace.contains("lorecore")) {
            this.parseKetherScript(namespace)
        } else {
            this.parseKetherScript(namespace.plus("lorecore"))
        }
    }

    /**
     * 运行脚本
     * */
    fun Script.runActions(func: ScriptContext.() -> Unit): CompletableFuture<Any?> {
        return try {
            ScriptContext.create(this).apply(func).runActions()
        } catch (e: Exception) {
            e.printKetherErrorMessage()
            CompletableFuture.completedFuture(null)
        }
    }

    /**
     * 运行脚本
     * */
    @Deprecated("")
    fun eval(
        script: String,
        sender: Any? = null,
        namespace: List<String> = listOf("lorecore"),
        args: Map<String, Any?>? = null,
        throws: Boolean = false
    ): CompletableFuture<Any?> {
        val func = {
            KetherShell.eval(script, sender = sender?.let { adaptCommandSender(it) }, namespace = namespace, context= {
                args?.forEach { (k, v) -> set(k, v) }
            })
        }
        return if (throws) func()
        else try {
            func()
        } catch (e: Exception) {
            e.printKetherErrorMessage()
            CompletableFuture.completedFuture(false)
        }
    }
}