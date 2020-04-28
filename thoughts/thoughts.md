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
with string, image. So, it should be customizable
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
