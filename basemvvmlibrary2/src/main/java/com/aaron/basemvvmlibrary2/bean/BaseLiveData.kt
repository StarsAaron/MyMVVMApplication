package com.aaron.basemvvmlibrary2.bean

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * 作者：Aaron
 * 时间：2019/10/24:17:20
 * 邮箱：
 * 说明：解决LiveData的粘性事件问题
 *
 * 普通事件是先注册，然后发送事件才能收到；而粘性事件，在发送事件之后再订阅该事件也能收到。
 *
 * 解决方法：
 * 1.既然每次注册时ObserverWrapper是不一样的，那么只要我们的LiveData也不一样不
 * 就可以轻松解决了
 * 2.不要多次注册:onCreate里面的注册以后，onResume里面就不要注册了，然后通过判
 * 断条件的不同写两个不同的处理方式。
 * 3.既然无法直接修改mVersion值和mLastVersion值。那么我们可以直接重写LiveData类。
 * 然后重写Observer接口，通过重写的Observer类的onChange方法进行拦截。那么怎么拦截？
 * 在LiveData的Observe方法里面将传入的Observer对象装饰到我们自己的Observer类里面，
 * 然后调用super.Observe的时候将我们自己的Observer方法传入，然后就可以进行自定义拦截。
 *
 * 参考链接：
 * https://www.jianshu.com/p/e08287ec62cd'>https://www.jianshu.com/p/e08287ec62cd
 * https://www.jianshu.com/p/f69e5f0dba9b
 */
class BaseLiveData<T> : MutableLiveData<T>() {
    internal var mVersion = START_VERSION

    /**
     * 重载方法
     * @param owner 被观察者
     * @param observer 观察者
     * @param isSticky 是否开启粘性事件处理
     */
    fun observe(owner: LifecycleOwner, observer: Observer<in T>, isSticky: Boolean) {
        if (isSticky) {
            super.observe(owner, observer)
        } else {
            super.observe(owner, CustomObserver(observer, this))
        }
    }

    override fun setValue(value: T) {
        mVersion++
        super.setValue(value)
    }

    override fun postValue(value: T) {
        mVersion++
        super.postValue(value)
    }

    internal inner class CustomObserver<T>(
        private val mObserver: Observer<in T>,
        private val liveData: BaseLiveData<T>
    ) : Observer<T> {
        // 通过标志位过滤旧数据
        private var mLastVersion: Int = 0

        init {
            this.mLastVersion = liveData.mVersion // 强行将mLastVersion跟mVersion置位相等，就能避免第一次的回调了
        }

        override fun onChanged(t: T) {
            //此处做拦截操作
            if (mLastVersion >= liveData.mVersion) {
                return
            }
            mLastVersion = liveData.mVersion
            mObserver.onChanged(t)
        }
    }

    companion object {
        private val START_VERSION = -1
    }
}