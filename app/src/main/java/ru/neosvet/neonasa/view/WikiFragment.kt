package ru.neosvet.neonasa.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.neosvet.neonasa.R


class WikiFragment : Fragment() {
    private lateinit var lSearch: TextInputLayout
    private lateinit var etSearch: TextInputEditText
    private lateinit var wvWiki: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wiki, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
            lSearch = findViewById(R.id.lSearch)
            etSearch = findViewById(R.id.etSearch)
            wvWiki = findViewById(R.id.wvWiki)
        }
        initSearch()
    }

    private fun initSearch() {
        wvWiki.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        })
        etSearch.setOnKeyListener(View.OnKeyListener { view, keyCode, keyEvent ->
            if (keyEvent.action === KeyEvent.ACTION_DOWN && keyEvent.keyCode === KeyEvent.KEYCODE_ENTER
                || keyCode == EditorInfo.IME_ACTION_SEARCH
            ) {
                startSearch()
                return@OnKeyListener true
            }
            false
        })
        lSearch.setEndIconOnClickListener {
            startSearch()
        }
    }

    private fun startSearch() {
        wvWiki.loadUrl("https://en.wikipedia.org/w/index.php?search=${etSearch.text.toString()}")
    }
}