package de.flapdoodle.einkauf

/*
 * Copyright (C) 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.lang.IllegalArgumentException
import java.text.DecimalFormat
import java.text.ParseException

/**
 * Activity for entering a word.
 */

class EditItemActivity : AppCompatActivity() {

    private lateinit var editNameView: EditText
    private lateinit var editPriceView: EditText

    private var name: String? = null
    private var price: MONEY? = null
    private var id: Int? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)
        editNameView = findViewById(R.id.edit_word)
        editPriceView = findViewById(R.id.edit_price)

        id = intent.getIntExtra(EXTRA_REPLY_ID,-1)
        if (id==-1) throw IllegalArgumentException("invalid id: $id")

        name = intent.getStringExtra(EXTRA_REPLY_NAME)
        price = intent.getIntExtra(EXTRA_REPLY_PRICE, 0)
        //getExtraData()

        editNameView.setText(name, TextView.BufferType.EDITABLE);
        editPriceView.setText(Numbers.amountAsString(price), TextView.BufferType.EDITABLE);


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (name==null || price==null) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                replyIntent.putExtra(EXTRA_REPLY_ID, id!!)
                replyIntent.putExtra(EXTRA_REPLY_NAME, name!!)
                replyIntent.putExtra(EXTRA_REPLY_PRICE, price!!)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }

        editNameView.addTextChangedListener(Validate {
            name = if (TextUtils.isEmpty(it)) null else it

            button.isEnabled  = name!=null && price!=null
        })
        editPriceView.addTextChangedListener(Validate {
            try {
                price = Numbers.parse(it)
            } catch (ex: ParseException) {
                price = null
                Toast.makeText(
                    applicationContext,
                    ex.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }

            button.isEnabled  = name!=null && price!=null
        })
    }

    companion object {
        const val EXTRA_REPLY_ID = "de.flapdoodle.einkauf.REPLY.id"
        const val EXTRA_REPLY_NAME = "de.flapdoodle.einkauf.REPLY.name"
        const val EXTRA_REPLY_PRICE = "de.flapdoodle.einkauf.REPLY.price"
    }

    class Validate(private val onChange: (String) -> Unit) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            onChange(editable.toString())
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

    }
}

