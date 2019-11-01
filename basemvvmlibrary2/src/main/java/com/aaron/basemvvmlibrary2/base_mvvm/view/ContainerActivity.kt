package com.aaron.basemvvmlibrary2.base_mvvm.view

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.aaron.basemvvmlibrary2.R
import java.lang.ref.WeakReference

/*
 * 盛装Fragment的一个容器(代理)Activity
 * 普通界面只需要编写Fragment,使用此Activity盛装,这样就不需要每个界面都在AndroidManifest中注册一遍
 *
 * 使用方法：
 * 在ViewModel中调用BaseViewModel的方法开一个Fragment
 * f_startContainerActivity(你的Fragment类名.class.getCanonicalName())
 *
 * 在ViewModel中调用BaseViewModel的方法，携带一个序列化实体打开一个Fragment
 * Bundle mBundle = new Bundle();
 * mBundle.putParcelable("entity", entity);
 * f_startContainerActivity(你的Fragment类名.class.getCanonicalName(), mBundle);
 *
 * 在你的Fragment中取出实体
 * Bundle mBundle = getArguments();
 * if (mBundle != null) {
 *      entity = mBundle.getParcelable("entity");
 * }
 *
 * 注意：
 *  * Fragment 需要提供一个f_isBackPressed方法返回用于返回按钮事件处理
 */
class ContainerActivity : AppCompatActivity() {
    private lateinit var mFragment: WeakReference<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        val fm = supportFragmentManager
        var fragment: Fragment? = null
        if (savedInstanceState != null) {
            fragment = fm.getFragment(savedInstanceState, FRAGMENT_TAG)
        }
        if (fragment == null) {
            fragment = initFromIntent(intent)
        }
        val trans = supportFragmentManager
            .beginTransaction()
        trans.replace(R.id.content, fragment)
        trans.commitAllowingStateLoss()
        mFragment = WeakReference(fragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mFragment.get()?.let { supportFragmentManager.putFragment(outState, FRAGMENT_TAG, it) }
    }

    protected fun initFromIntent(data: Intent?): Fragment {
        if (data == null) {
            throw RuntimeException(
                "you must provide a page info to display"
            )
        }
        try {
            val fragmentName = data.getStringExtra(FRAGMENT)
            require(!(fragmentName == null || "" == fragmentName)) { "can not find page fragmentName" }
            val fragmentClass = Class.forName(fragmentName)
            val fragment = fragmentClass.newInstance() as Fragment
            val args = data.getBundleExtra(BUNDLE)
            if (args != null) {
                fragment.arguments = args
            }
            return fragment
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        throw RuntimeException("fragment initialization failed!")
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.content)
        if (fragment is MultiBaseMVVMFragment<*>) {
            if (!fragment.f_isBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private val FRAGMENT_TAG = "content_fragment_tag"
        val FRAGMENT = "fragment"
        val BUNDLE = "bundle"
    }
}
