package com.jetbrains.rider.framework.test.util

import com.jetbrains.rider.framework.*
import com.jetbrains.rider.framework.base.RdBindableBase
import com.jetbrains.rider.framework.impl.RdProperty
import com.jetbrains.rider.util.lifetime.Lifetime
import com.jetbrains.rider.util.reactive.IProperty
import kotlin.reflect.KClass


class DynamicEntity<T>(val _foo: RdProperty<T>) : RdBindableBase() {
    val foo: IProperty<T> = _foo

    companion object : IMarshaller<DynamicEntity<*> > {
        override fun read(ctx: SerializationCtx, buffer: AbstractBuffer): DynamicEntity<*> {
            return DynamicEntity(RdProperty.read(ctx, buffer))
        }

        override fun write(ctx: SerializationCtx, buffer: AbstractBuffer, value: DynamicEntity<*>) {
            RdProperty.write(ctx, buffer, value._foo)
        }

        override val _type: KClass<*>
            get() = DynamicEntity::class

        fun create(protocol: IProtocol) {
            protocol.serializers.register(DynamicEntity)
        }
    }

    override fun init(lifetime: Lifetime) {
        _foo.bind(lifetime, this, "foo")
    }

    override fun identify(identities: IIdentities, id: RdId) {
        _foo.identify(identities, id.mix("foo"))
    }

    constructor(_foo: T) : this(RdProperty<T>(_foo, Polymorphic<T>()))
}