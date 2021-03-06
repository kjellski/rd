package com.jetbrains.rider.framework.base

import com.jetbrains.rider.framework.IIdentities
import com.jetbrains.rider.framework.IRdDynamic
import com.jetbrains.rider.framework.RdId
import com.jetbrains.rider.util.lifetime.Lifetime

/**
 * A non-root node in an object graph which can be synchronized with its remote copy over a network or a similar connection.
 */
interface IRdBindable : IRdDynamic {
    val rdid: RdId

    /**
     * Inserts the node into the object graph under the given [parent] and assigns the specified [name] to it. The node will
     * be removed from the graph when the specified [lf] lifetime is terminated.
     */
    fun bind(lf: Lifetime, parent: IRdDynamic, name: String)

    /**
     * Assigns IDs to this node and its child nodes in the graph.
     */
    fun identify(identities: IIdentities, id: RdId)
}

//generator comprehension methods
fun <T:IRdBindable?> T.bind(lf: Lifetime, parent: IRdDynamic, name: String) = this?.bind(lf, parent, name)
fun <T:IRdBindable?> T.identify(identities: IIdentities, ids: RdId) = this?.identify(identities, ids)

fun <T:IRdBindable?> Array<T>.identify(identities: IIdentities, ids: RdId) = forEachIndexed { i, v ->  v?.identify(identities, ids.mix(i))}
fun <T:IRdBindable?> Array<T>.bind(lf: Lifetime, parent: IRdDynamic, name: String) = forEachIndexed { i, v ->  v?.bind(lf,parent, "$name[$i]")}

fun <T:IRdBindable?> List<T>.identify(identities: IIdentities, ids: RdId) = forEachIndexed { i, v ->  v?.identify(identities, ids.mix(i))}
fun <T:IRdBindable?> List<T>.bind(lf: Lifetime, parent: IRdDynamic, name: String) = forEachIndexed { i, v ->  v?.bind(lf,parent, "$name[$i]")}

internal fun Any.identifyPolymorphic(identities: IIdentities, ids: RdId) {
    if (this is IRdBindable) {
        this.identify(identities, ids)
    } else {
        (this as? Array<*>)?.forEachIndexed { i, v  ->  (v as? IRdBindable)?.identify(identities, ids.mix(i))}
        (this as? List<*>)?.forEachIndexed { i, v  ->  (v as? IRdBindable)?.identify(identities, ids.mix(i))}
    }

}

internal fun Any.bindPolymorphic(lf: Lifetime, parent: IRdDynamic, name: String) {
    if (this is IRdBindable)
        this.bind(lf, parent, name)
    else {
        //Don't remove 'else'. RdList is bindable and collection simultaneously.
        (this as? Array<*>)?.forEachIndexed { i, v ->  (v as? IRdBindable)?.bind(lf,parent, "$name[$i]")}
        (this as? List<*>)?.forEachIndexed { i, v ->  (v as? IRdBindable)?.bind(lf,parent, "$name[$i]")}
    }

}
