# References:
1. The "box" refers to some place where
you can type something. Like an `EditText`

# Thoughts

So, the idea is to be able

1. to type some text in the box
2. when typing "@", we should be able to choose
from a list of suggestions to put that content
into the box.
3. The items are normal strings. They can contain
spaces though. Later, the items will be complex -
with string, image. So, the view of the list and
the list items should be customizable
4. And when the item is selected - only then it is
put in the box. Otherwise not. 
5. When the item is put in the box, it should be
highlighted in some color

---
First Attempt:
1. So I tried the `MultiAutoCompleteTextView`. Tried the example here

https://developer.android.com/reference/android/widget/MultiAutoCompleteTextView.html

I'm able to type stuff and I'm able to see some suggestions sometimes.
Suggestions for country names. Like `Belgium`, `Spain` and more from
the list I had already provided. I can see suggestions at the start
of the box - like when there is no text, but just spaces may be.
More like this

This gave suggestions -

```
Be
```

This too -

```
      be
```

But not this. Since it was only one character -

```
B
```

The below too didn't give suggestions. I think this was because
there were other words before.

```
okay okay okay bel
```

I see that we have some tokens and tokenizing concept and in my case
it's a comma tokenizer

```
textView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
```

so, I can see suggestions when I type command `,`. Like this below,
I get suggestions for the second `be`

```
okay okay okay bel, be
```

Demo video:
https://www.youtube.com/watch?v=j3rLYAs4mvk

---
From the first attempt, I'm trying to understand when the suggestions
are shown. Looks like the suggestions are shown based on the tokenization
concept. I can see there's a comma tokenizer and I also see an interface
for the tokenizer

```
public static interface Tokenizer {
    /**
     * Returns the start of the token that ends at offset
     * <code>cursor</code> within <code>text</code>.
     */
    public int findTokenStart(CharSequence text, int cursor);

    /**
     * Returns the end of the token (minus trailing punctuation)
     * that begins at offset <code>cursor</code> within <code>text</code>.
     */
    public int findTokenEnd(CharSequence text, int cursor);

    /**
     * Returns <code>text</code>, modified, if necessary, to ensure that
     * it ends with a token terminator (for example a space or comma).
     */
    public CharSequence terminateToken(CharSequence text);
}
```

I checked the code for comma tokenizer's `findTokenStart` method.
After checking code and the above docs, looks to me like this is
what it is -

`findTokenStart` takes a sequence of characters (like a string),
and a cursor offset, more like the index in a string. The cursor
(the index) is at the end of a token, and now, with this information,
you need to find the start of the token, the index of the start
of the token in the sequence. That's what `findTokenStart` is about

In the case of comma tokenizer's method I can see that the text
is traversed backwards to reach just after the previous comma,
as comma is the token separator I guess. And then the spaces
after that index are ignored and then we land at the start
of the token. The implementation -

```
public int findTokenStart(CharSequence text, int cursor) {
    int i = cursor;

    while (i > 0 && text.charAt(i - 1) != ',') {
        i--;
    }
    while (i < cursor && text.charAt(i) == ' ') {
        i++;
    }

    return i;
}
```

Similarly I checked the code for `findTokenEnd` method in comma tokenizer.
This time the cursor is at the start of the token, the start index,
and we gotta find the end of the token, the end index. Looking at
comma tokenizer's implementation, it looks like the index of the
`,` is what is sent and not the character before the `,`. Interesting.


```
public int findTokenEnd(CharSequence text, int cursor) {
    int i = cursor;
    int len = text.length();

    while (i < len) {
        if (text.charAt(i) == ',') {
            return i;
        } else {
            i++;
        }
    }

    return len;
}
```

Finally I checked the `terminateToken` method too, in comma tokenizer.
This one helps in putting a token terminator character to a token, if one
does not exist. The code is mostly simple except for one part which I didn't
get. Something about `Spanned` and `SpannableString`. Not sure why it's needed.
I'm hoping I don't need it, so not gonna look more into. I think I can handle it
if I face issues later, regarding that.

```
public CharSequence terminateToken(CharSequence text) {
    int i = text.length();

    while (i > 0 && text.charAt(i - 1) == ' ') {
        i--;
    }

    if (i > 0 && text.charAt(i - 1) == ',') {
        return text;
    } else {
        if (text instanceof Spanned) {
            SpannableString sp = new SpannableString(text + ", ");
            TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                                    Object.class, sp, 0);
            return sp;
        } else {
            return text + ", ";
        }
    }
}
```

The part I didn't understand was why was this line needed -

```
TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                                    Object.class, sp, 0);
```

Especially when we already have created `sp` already with this line,
which looks good to me.

```
SpannableString sp = new SpannableString(text + ", ");
```

Like I said, I'll come back to it later in case of any issues.

---
Now, I'm planning to create a tokenizer of my own! The plan is to
show suggestions when `@` is typed! :) And to indicate the end of
a token, I'm going to use the character `US` - `Unit Separator`
that me and my friend recently saw in this code base

https://github.com/hootsuite/nachos

Let's see how this tokenizer thing goes! :)

Gonna first copy the comma tokenizer! ;)

Okay, I copied and made some changes. It looks weird now
actually. Lol :P

Demo
https://youtu.be/KA8S2orq8NQ

So, that was the second attempt
---

Clearly, we can see some weird character at the end of
the token when choosing from the list. Also, we can
see suggestions at the start with no @. I think I know
the reason for both. Need to see how to fix it though

---

Third attempt

Let's try to fix the issues that we just noticed.

So, the first issue was - seeing suggestions while
starting to type, even when no `@` was given.
I "think" this was because of the fact that the
`findTokenStart` returned `0` even when there
was no `@` at the start and it started giving
suggestions. So, I made sure I returned the
`cursor` value as is, if no `@` was found
anywhere, till the start, including the
index `0`.

So, this fixed one bug! :)

