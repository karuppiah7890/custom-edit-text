package com.example.custom_edit_text

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView.Tokenizer
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line, entities
        )
        textView.setAdapter(adapter)
        textView.setTokenizer(MentionTokenizer())
    }

    private val entities = arrayOf(
        "Karuppiah Natarajan",
        "Karthik",
        "Dharamadurai",
        "Alex",
        "Bharath",
        "Karuppiah",
        "Bharath Nepolean"
    )

}

// MentionTokenizer is a tokenizer that can be used for lists
// where the list contains entities that can be mentioned
// using @ symbol in a text box
class MentionTokenizer : Tokenizer {
    // ASCII code representing ETX - End of Text
    // http://www.asciitable.com/
    private val endOfTokenCode = 3
    private val endOfTextChar = endOfTokenCode.toChar()
    private val startOfTokenChar = '@'

    override fun findTokenStart(text: CharSequence, cursor: Int): Int {
        var i = cursor
        while (i > 0) {
            if (text[i - 1] == startOfTokenChar) {
                return i
            }
            i--
        }
        if (i == 0 && text.isNotEmpty() && text[0] == startOfTokenChar) {
            return 0
        }
        return cursor
    }

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
        var i = cursor
        val len = text.length
        while (i < len) {
            if (text[i] == endOfTextChar) {
                return i
            } else {
                i++
            }
        }
        return len
    }

    override fun terminateToken(text: CharSequence): CharSequence {
        val i = text.length
        return if (i > 0 && text[i - 1] == endOfTextChar) {
            text
        } else {
            "$text$endOfTextChar"
        }
    }
}