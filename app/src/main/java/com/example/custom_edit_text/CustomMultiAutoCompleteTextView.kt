package com.example.custom_edit_text

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView

class CustomMultiAutoCompleteTextView : AppCompatMultiAutoCompleteTextView, TextWatcher {
    private var ignoreTextChangedEvents: Boolean = false
    private val endOfTokenCode = 3
    private val endOfTextChar = endOfTokenCode.toChar()
    private val startOfTokenChar = '@'

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable) {
        if (ignoreTextChangedEvents) {
            return
        }
    }

    private fun endUnwatchedTextChange() {
        ignoreTextChangedEvents = false
    }

    private fun beginUnwatchedTextChange() {
        ignoreTextChangedEvents = true
    }

    override fun onTextChanged(
        textChanged: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
        if (ignoreTextChangedEvents) {
            return
        }

        beginUnwatchedTextChange()
        val indexListForSpan = mutableListOf<MentionIndexRange>()

        val iter = text.withIndex().iterator()
        var mentionStartIndex = 0
        var lookingForMentionStart = true
        while (iter.hasNext()) {
            val next = iter.next()
            if (next.value == startOfTokenChar) {
                lookingForMentionStart = false
                mentionStartIndex = next.index
                continue
            } else if (next.value == endOfTextChar) {
                if (!lookingForMentionStart) {
                    val mentionEndIndex = next.index
                    indexListForSpan.add(MentionIndexRange(mentionStartIndex, mentionEndIndex))
                    lookingForMentionStart = true
                }
            }
        }

        val spans = text.getSpans(0, text.length, BackgroundColorSpan::class.java)
        spans.forEach { span -> text.removeSpan(span) }
        val indexListIter = indexListForSpan.iterator()
        while (indexListIter.hasNext()) {
            val next = indexListIter.next()
            text.setSpan(
                BackgroundColorSpan(Color.parseColor("#90EE90")),
                next.start,
                next.end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        endUnwatchedTextChange()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (ignoreTextChangedEvents) {
            return
        }
    }
}

class MentionIndexRange(val start: Int, val end: Int) {}