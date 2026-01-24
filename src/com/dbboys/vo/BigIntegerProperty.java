package com.dbboys.vo;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import java.math.BigInteger;

public class BigIntegerProperty extends ReadOnlyObjectWrapper<BigInteger> {

    // 基础构造函数
    public BigIntegerProperty() {
        super(BigInteger.ZERO);
    }

    public BigIntegerProperty(BigInteger initialValue) {
        super(initialValue);
    }

    public BigIntegerProperty(Object bean, String name) {
        super(bean, name, BigInteger.ZERO);
    }

    public BigIntegerProperty(Object bean, String name, BigInteger initialValue) {
        super(bean, name, initialValue);
    }

    // 加法运算
    public ObjectBinding<BigInteger> add(BigInteger other) {
        return Bindings.createObjectBinding(() -> get().add(other), this);
    }

    public ObjectBinding<BigInteger> add(ObservableValue<BigInteger> other) {
        return Bindings.createObjectBinding(() -> get().add(other.getValue()), this, other);
    }

    // 减法运算
    public ObjectBinding<BigInteger> subtract(BigInteger other) {
        return Bindings.createObjectBinding(() -> get().subtract(other), this);
    }

    public ObjectBinding<BigInteger> subtract(ObservableValue<BigInteger> other) {
        return Bindings.createObjectBinding(() -> get().subtract(other.getValue()), this, other);
    }

    // 乘法运算
    public ObjectBinding<BigInteger> multiply(BigInteger other) {
        return Bindings.createObjectBinding(() -> get().multiply(other), this);
    }

    public ObjectBinding<BigInteger> multiply(ObservableValue<BigInteger> other) {
        return Bindings.createObjectBinding(() -> get().multiply(other.getValue()), this, other);
    }

    // 除法运算
    public ObjectBinding<BigInteger> divide(BigInteger other) {
        return Bindings.createObjectBinding(() -> get().divide(other), this);
    }

    public ObjectBinding<BigInteger> divide(ObservableValue<BigInteger> other) {
        return Bindings.createObjectBinding(() -> get().divide(other.getValue()), this, other);
    }

    // 其他常用操作
    public ObjectBinding<BigInteger> negate() {
        return Bindings.createObjectBinding(() -> get().negate(), this);
    }

    public ObjectBinding<Integer> compareTo(BigInteger other) {
        return Bindings.createObjectBinding(() -> get().compareTo(other), this);
    }

    public ObjectBinding<Integer> compareTo(ObservableValue<BigInteger> other) {
        return Bindings.createObjectBinding(() -> get().compareTo(other.getValue()), this, other);
    }

    // 支持 Number 类型设置值
    public void setValue(Number value) {
        set(BigInteger.valueOf(value.longValue()));
    }

    // 双向绑定支持
    public void bindBidirectional(ObjectProperty<BigInteger> other) {
        Bindings.bindBidirectional(this, other);
    }

    public void unbindBidirectional(ObjectProperty<BigInteger> other) {
        Bindings.unbindBidirectional(this, other);
    }
}
