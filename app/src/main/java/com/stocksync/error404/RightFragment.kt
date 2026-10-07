package com.stocksync.error404

import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class RightFragment : Fragment(R.layout.fragment_right) {

    private lateinit var contentLayout: LinearLayout

    private var videoView: VideoView? = null
    private var webView: WebView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentLayout = view.findViewById(R.id.contentLayout)
        showOption("Inicio")
    }

    private fun ImageView.loadUrl(urlString: String) {
        setImageResource(android.R.drawable.ic_menu_gallery)
        if (urlString.isEmpty()) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)
                withContext(Dispatchers.Main) {
                    if (bitmap != null) {
                        setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                // fallback
            }
        }
    }

    private fun showImageDialog(imageUrl: String, title: String) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#CC000000"))
            setPadding(16, 32, 16, 16)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Botón X de cierre
        val closeBtn = Button(requireContext()).apply {
            text = "✕ Cerrar"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setBackgroundColor(Color.parseColor("#D84F52"))
            setTextColor(Color.WHITE)
            setOnClickListener { dialog.dismiss() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.END
                bottomMargin = 16
            }
        }

        // Título del producto
        val titleView = TextView(requireContext()).apply {
            text = title
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }

        // Imagen grande responsive (se ajusta al tamaño de pantalla)
        val largeImageView = ImageView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            ).apply { bottomMargin = 24 }
            scaleType = ImageView.ScaleType.FIT_CENTER
            loadUrl(imageUrl)
        }

        layout.addView(closeBtn)
        layout.addView(titleView)
        layout.addView(largeImageView)

        dialog.setContentView(layout)
        dialog.show()
    }

    fun showOption(option: String) {
        if (!::contentLayout.isInitialized) return

        releaseMedia()
        contentLayout.removeAllViews()

        when {
            option == "Inicio" -> renderHome()
            option == "Mi Cuenta" || option == "Perfil" -> renderProfile()
            option == "Catálogo" || option == "Fotos" -> renderPhotos()
            option == "Videos" || option == "Video" -> renderVideo()
            option == "Web" -> renderWeb()
            option == "Carrito" || option == "Botones" -> renderCart()
            option == "Favoritos" -> renderFavorites()
            option == "Categorías" -> renderCategoriesOverview()
            option.startsWith("Categoría: ") -> {
                val catName = option.substringAfter("Categoría: ")
                renderCategory(catName)
            }
            else -> renderHome()
        }
    }

    private fun renderHome() {
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val mainContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // 1. Search Bar (Functional EditText with live suggestions)
        val searchContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
        }

        val searchInputBox = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(12, 8, 12, 8)
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.parseColor("#F2F4F7"))
        }

        val searchIcon = TextView(requireContext()).apply {
            text = "🔍 "
            textSize = 16f
        }

        val searchEditText = EditText(requireContext()).apply {
            hint = "Buscar productos, marcas o categorías"
            textSize = 14f
            setBackgroundColor(Color.TRANSPARENT)
            setTextColor(Color.parseColor("#000000"))
            setHintTextColor(Color.parseColor("#888888"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        searchInputBox.addView(searchIcon)
        searchInputBox.addView(searchEditText)
        searchContainer.addView(searchInputBox)

        val suggestionsList = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FFFFFF"))
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        searchContainer.addView(suggestionsList)

        val allSearchableItems = listOf(
            "Smartwatch Pro" to "Electrónica",
            "Audífonos X9" to "Electrónica",
            "Laptop Aero" to "Electrónica",
            "Cámara Mini" to "Electrónica",
            "Gafas VR" to "Electrónica",
            "Lámpara LED" to "Hogar",
            "Cafetera" to "Hogar",
            "Juego de Sábanas" to "Hogar",
            "Chaqueta Denim" to "Moda",
            "Tenis Urban" to "Moda",
            "Gorra Sync" to "Moda",
            "Balón de Fútbol Pro" to "Deportes",
            "Raqueta de Tenis" to "Deportes",
            "Guantes de Gym" to "Deportes",
            "Smartphone Ultra 128GB" to "Destacados",
            "Audífonos Inalámbricos Pro" to "Destacados",
            "Categoría: Electrónica" to "Categoría",
            "Categoría: Hogar" to "Categoría",
            "Categoría: Moda" to "Categoría",
            "Categoría: Deportes" to "Categoría"
        )

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                suggestionsList.removeAllViews()

                if (query.isNotEmpty()) {
                    val filtered = allSearchableItems.filter { it.first.contains(query, ignoreCase = true) }
                    if (filtered.isNotEmpty()) {
                        suggestionsList.visibility = View.VISIBLE
                        filtered.forEach { (name, type) ->
                            val itemLayout = LinearLayout(requireContext()).apply {
                                orientation = LinearLayout.HORIZONTAL
                                setPadding(16, 12, 16, 12)
                                gravity = Gravity.CENTER_VERTICAL
                                setBackgroundColor(Color.parseColor("#FAFAFA"))
                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    bottomMargin = 2
                                }
                                setOnClickListener {
                                    searchEditText.setText(name)
                                    suggestionsList.visibility = View.GONE
                                    if (type == "Categoría") {
                                        showOption(name)
                                    } else {
                                        Toast.makeText(requireContext(), "Seleccionado: $name ($type)", Toast.LENGTH_SHORT).show()
                                        showOption("Catálogo")
                                    }
                                }
                            }
                            val nameView = TextView(requireContext()).apply {
                                text = name
                                textSize = 14f
                                typeface = Typeface.DEFAULT_BOLD
                                setTextColor(Color.parseColor("#000000"))
                                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            }
                            val typeView = TextView(requireContext()).apply {
                                text = type
                                textSize = 12f
                                setTextColor(Color.parseColor("#D84F52"))
                            }
                            itemLayout.addView(nameView)
                            itemLayout.addView(typeView)
                            suggestionsList.addView(itemLayout)
                        }
                    } else {
                        suggestionsList.visibility = View.VISIBLE
                        val noResult = TextView(requireContext()).apply {
                            text = "No se encontraron resultados"
                            textSize = 14f
                            setTextColor(Color.parseColor("#777777"))
                            setPadding(16, 12, 16, 12)
                        }
                        suggestionsList.addView(noResult)
                    }
                } else {
                    suggestionsList.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        mainContainer.addView(searchContainer)

        // 2. Offer Banner
        val bannerCard = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.parseColor("#D84F52"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
            setOnClickListener {
                Toast.makeText(requireContext(), "¡Oferta del Día seleccionada!", Toast.LENGTH_SHORT).show()
                showOption("Categoría: Electrónica")
            }
        }
        val badgeText = TextView(requireContext()).apply {
            text = "OFERTA DEL DÍA"
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#FFECEC"))
            setPadding(8, 4, 8, 4)
            setBackgroundColor(Color.parseColor("#B53B3E"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }
        val bannerTitle = TextView(requireContext()).apply {
            text = "Hasta 50% dcto en Electrónica"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }
        val bannerDesc = TextView(requireContext()).apply {
            text = "Smartphones, notebooks, audífonos y accesorios con envío rápido."
            textSize = 13f
            setTextColor(Color.parseColor("#FFF0F0"))
        }
        bannerCard.addView(badgeText)
        bannerCard.addView(bannerTitle)
        bannerCard.addView(bannerDesc)
        mainContainer.addView(bannerCard)

        // 3. Categories Title & Row
        val catTitle = TextView(requireContext()).apply {
            text = "Categorías"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }
        mainContainer.addView(catTitle)

        val categoriesRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        val cats = listOf("Hogar", "Moda", "Electrónica", "Deportes")
        val catIcons = listOf("🏠", "👕", "📱", "⚽")
        cats.forEachIndexed { index, cat ->
            val catCard = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(12, 16, 12, 16)
                setBackgroundColor(Color.parseColor("#F2F4F7"))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = if (index < cats.size - 1) 8 else 0
                }
                setOnClickListener {
                    showOption("Categoría: $cat")
                }
            }
            val iconView = TextView(requireContext()).apply {
                text = catIcons[index]
                textSize = 22f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 6
                }
            }
            val labelView = TextView(requireContext()).apply {
                text = cat
                textSize = 12f
                setTextColor(Color.parseColor("#333333"))
                gravity = Gravity.CENTER
            }
            catCard.addView(iconView)
            catCard.addView(labelView)
            categoriesRow.addView(catCard)
        }
        mainContainer.addView(categoriesRow)

        // 4. Featured Products Title & Grid
        val prodHeaderLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }
        val prodTitle = TextView(requireContext()).apply {
            text = "Productos Destacados"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val verTodo = TextView(requireContext()).apply {
            text = "Ver todo"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#D84F52"))
            setOnClickListener {
                showOption("Catálogo")
            }
        }
        prodHeaderLayout.addView(prodTitle)
        prodHeaderLayout.addView(verTodo)
        mainContainer.addView(prodHeaderLayout)

        val productsRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        val products = listOf(
            Triple("Smartphone Ultra 128GB", "$249.990", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=300&q=80"),
            Triple("Audífonos Inalámbricos Pro", "$89.990", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&q=80")
        )

        products.forEachIndexed { index, (name, price, imageUrl) ->
            val pCard = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 12)
                setBackgroundColor(Color.parseColor("#F2F4F7"))
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    marginEnd = if (index < products.size - 1) 12 else 0
                }
            }

            val pImageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    180
                ).apply { bottomMargin = 8 }
                scaleType = ImageView.ScaleType.CENTER_CROP
                loadUrl(imageUrl)
                setOnClickListener {
                    showImageDialog(imageUrl, name)
                }
            }

            val pName = TextView(requireContext()).apply {
                text = name
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#000000"))
                maxLines = 2
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 6
                }
            }
            val priceRow = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val pPrice = TextView(requireContext()).apply {
                text = price
                textSize = 15f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#D84F52"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
                }
            }
            priceRow.addView(pPrice)

            val addCartBtn = Button(requireContext()).apply {
                text = "🛒 Agregar"
                textSize = 11f
                setBackgroundColor(Color.parseColor("#D84F52"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    CartRepository.addItem(CartItem(name, price, "Producto Destacado", imageUrl))
                    Toast.makeText(requireContext(), "¡$name agregado al carrito!", Toast.LENGTH_SHORT).show()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8
                }
            }

            val addFavBtn = Button(requireContext()).apply {
                text = "⭐ Favorito"
                textSize = 11f
                setBackgroundColor(Color.parseColor("#2B8FA0"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    FavoritesRepository.addItem(FavoriteItem(name, price, "Producto Destacado", imageUrl))
                    Toast.makeText(requireContext(), "¡$name agregado a Favoritos!", Toast.LENGTH_SHORT).show()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 4
                }
            }

            pCard.addView(pImageView)
            pCard.addView(pName)
            pCard.addView(priceRow)
            pCard.addView(addCartBtn)
            pCard.addView(addFavBtn)
            productsRow.addView(pCard)
        }
        mainContainer.addView(productsRow)

        // 5. Flash Sale Card
        val flashCard = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(Color.parseColor("#F2F4F7"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                Toast.makeText(requireContext(), "¡Flash Sale seleccionada!", Toast.LENGTH_SHORT).show()
            }
        }
        val flashInfo = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        val flashHeader = TextView(requireContext()).apply {
            text = "Flash Sale"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 4
            }
        }
        val flashDesc = TextView(requireContext()).apply {
            text = "Termina en menos de 2 horas.\nCompra ahora y aprovecha."
            textSize = 12f
            setTextColor(Color.parseColor("#555555"))
        }
        flashInfo.addView(flashHeader)
        flashInfo.addView(flashDesc)

        val timerBadge = TextView(requireContext()).apply {
            text = "01:48:32"
            textSize = 14f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            setPadding(12, 8, 12, 8)
            setBackgroundColor(Color.parseColor("#D84F52"))
        }

        flashCard.addView(flashInfo)
        flashCard.addView(timerBadge)
        mainContainer.addView(flashCard)

        scrollView.addView(mainContainer)
        contentLayout.addView(scrollView)
    }

    private fun renderCategoriesOverview() {
        val scrollView = ScrollView(requireContext())
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(requireContext()).apply {
            text = "Categorías Principales"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        container.addView(title)

        val cats = listOf("Electrónica", "Hogar", "Moda", "Deportes")
        val catIcons = mapOf("Electrónica" to "📱", "Hogar" to "🏠", "Moda" to "👕", "Deportes" to "⚽")

        cats.forEach { cat ->
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
                gravity = Gravity.CENTER_VERTICAL
                setBackgroundColor(Color.parseColor("#F2F4F7"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
                setOnClickListener {
                    showOption("Categoría: $cat")
                }
            }

            val iconView = TextView(requireContext()).apply {
                text = catIcons[cat] ?: "📦"
                textSize = 24f
                setPadding(0, 0, 16, 0)
            }

            val textView = TextView(requireContext()).apply {
                text = cat
                textSize = 18f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#000000"))
            }

            card.addView(iconView)
            card.addView(textView)
            container.addView(card)
        }

        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private fun renderCategory(category: String) {
        val scrollView = ScrollView(requireContext())
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(requireContext()).apply {
            text = "Categoría: $category"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        container.addView(title)

        val products = when (category) {
            "Electrónica" -> listOf(
                ProductData("Smartwatch Pro", "$450.000", "Reloj inteligente para entrenamientos y notificaciones.", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300&q=80"),
                ProductData("Audífonos X9", "$120.000", "Sonido premium con batería de larga duración.", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&q=80"),
                ProductData("Laptop Aero", "$1.800.000", "Portátil ligera para estudio y trabajo diario.", "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=300&q=80"),
                ProductData("Cámara Mini", "$350.000", "Captura fotos y videos con calidad profesional.", "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=300&q=80"),
                ProductData("Gafas VR", "$290.000", "Experiencia inmersiva para entretenimiento y gaming.", "https://images.unsplash.com/photo-1593508512255-86ab42a8e620?w=300&q=80")
            )
            "Hogar" -> listOf(
                ProductData("Lámpara LED", "$85.000", "Iluminación inteligente y de bajo consumo para tu hogar.", "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=300&q=80"),
                ProductData("Cafetera", "$190.000", "Café fresco y recién hecho todas las mañanas.", "https://images.unsplash.com/photo-1620807773206-49c1f2957417?w=300&q=80"),
                ProductData("Juego de Sábanas", "$150.000", "Suavidad y confort para un descanso óptimo.", "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=300&q=80")
            )
            "Moda" -> listOf(
                ProductData("Chaqueta Denim", "$120.000", "Estilo clásico y duradero para cualquier ocasión.", "https://images.unsplash.com/photo-1543076447-215ad9ba6923?w=300&q=80"),
                ProductData("Tenis Urban", "$210.000", "Comodidad y diseño moderno para el día a día.", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=300&q=80"),
                ProductData("Gorra Sync", "$35.000", "Protección y estilo urbano con ajuste perfecto.", "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=300&q=80")
            )
            "Deportes" -> listOf(
                ProductData("Balón de Fútbol Pro", "$95.000", "Balón oficial con costuras reforzadas para alta durabilidad.", "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=300&q=80"),
                ProductData("Raqueta de Tenis", "$210.000", "Raqueta ligera de grafito con encordado profesional.", "https://plus.unsplash.com/premium_photo-1666913667082-c1fecc45275d?w=300&q=80"),
                ProductData("Guantes de Gym", "$45.000", "Protección acolchada para entrenamiento con pesas.", "https://images.unsplash.com/photo-1557127972-1c446ea89ea5?w=300&q=80")
            )
            else -> emptyList()
        }

        products.forEach { prod ->
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 12)
                setBackgroundColor(Color.parseColor("#F2F4F7"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            }

            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    220
                ).apply { bottomMargin = 8 }
                scaleType = ImageView.ScaleType.CENTER_CROP
                loadUrl(prod.imageUrl)
                setOnClickListener {
                    showImageDialog(prod.imageUrl, prod.title)
                }
            }

            val titleView = TextView(requireContext()).apply {
                text = prod.title
                typeface = Typeface.DEFAULT_BOLD
                textSize = 18f
                setTextColor(Color.parseColor("#000000"))
            }

            val priceView = TextView(requireContext()).apply {
                text = prod.price
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#D84F52"))
            }

            val descView = TextView(requireContext()).apply {
                text = prod.description
                textSize = 14f
                setTextColor(Color.parseColor("#333333"))
            }

            val addBtn = Button(requireContext()).apply {
                text = "🛒 Agregar al carrito"
                textSize = 12f
                setBackgroundColor(Color.parseColor("#D84F52"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    CartRepository.addItem(CartItem(prod.title, prod.price, prod.description, prod.imageUrl))
                    Toast.makeText(requireContext(), "¡${prod.title} agregado al carrito!", Toast.LENGTH_SHORT).show()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8
                }
            }

            val favBtn = Button(requireContext()).apply {
                text = "⭐ Agregar a favoritos"
                textSize = 12f
                setBackgroundColor(Color.parseColor("#2B8FA0"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    FavoritesRepository.addItem(FavoriteItem(prod.title, prod.price, prod.description, prod.imageUrl))
                    Toast.makeText(requireContext(), "¡${prod.title} agregado a Favoritos!", Toast.LENGTH_SHORT).show()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 6
                }
            }

            card.addView(imageView)
            card.addView(titleView)
            card.addView(priceView)
            card.addView(descView)
            card.addView(addBtn)
            card.addView(favBtn)
            container.addView(card)
        }

        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private data class ProductData(val title: String, val price: String, val description: String, val imageUrl: String)

    private fun releaseMedia() {
        videoView?.stopPlayback()
        videoView = null

        webView?.let { web ->
            web.stopLoading()
            web.loadUrl("about:blank")
            (web.parent as? ViewGroup)?.removeView(web)
            web.destroy()
        }
        webView = null
    }

    override fun onDestroyView() {
        releaseMedia()
        super.onDestroyView()
    }

    private fun renderProfile() {
        val profileText = TextView(requireContext()).apply {
            text = "Nombre: Camila López\n\n" +
                "Estudios: Ingeniería de Sistemas - Politécnico Grancolombiano\n\n" +
                "Experiencia: 5 años en analítica comercial y atención al cliente\n\n" +
                "Especialidad: e-commerce, marketing digital y atención personalizada\n\n" +
                "Perfil profesional: Apasionada por crear experiencias de compra intuitivas, " +
                "rápidas y seguras para clientes que buscan productos con valor y confianza. " +
                "Su enfoque combina tecnología, servicio y estrategia para ofrecer una mejor " +
                "atención digital.\n\n" +
                "Habilidades: atención al cliente, ventas, investigación de mercado, " +
                "diseño de contenido, analítica y gestión de campañas."
            textSize = 16f
            setTextColor(Color.parseColor("#000000"))
            setPadding(16, 16, 16, 16)
            movementMethod = ScrollingMovementMethod()
        }

        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        container.addView(profileText)
        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private fun renderPhotos() {
        val scrollView = ScrollView(requireContext())
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val titleView = TextView(requireContext()).apply {
            text = "Catálogo General (Todos los productos)"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        container.addView(titleView)

        val allProducts = listOf(
            ProductData("Smartwatch Pro", "$450.000", "Reloj inteligente para entrenamientos y notificaciones.", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=300&q=80"),
            ProductData("Audífonos X9", "$120.000", "Sonido premium con batería de larga duración.", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=300&q=80"),
            ProductData("Laptop Aero", "$1.800.000", "Portátil ligera para estudio y trabajo diario.", "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=300&q=80"),
            ProductData("Cámara Mini", "$350.000", "Captura fotos y videos con calidad profesional.", "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=300&q=80"),
            ProductData("Gafas VR", "$290.000", "Gafas de realidad virtual inmersiva.", "https://images.unsplash.com/photo-1593508512255-86ab42a8e620?w=300&q=80"),
            ProductData("Lámpara LED", "$85.000", "Iluminación inteligente y de bajo consumo.", "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=300&q=80"),
            ProductData("Cafetera", "$190.000", "Café fresco y recién hecho todas las mañanas.", "https://images.unsplash.com/photo-1620807773206-49c1f2957417?w=300&q=80"),
            ProductData("Juego de Sábanas", "$150.000", "Suavidad y confort para un descanso óptimo.", "https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=300&q=80"),
            ProductData("Chaqueta Denim", "$120.000", "Estilo clásico y duradero para cualquier ocasión.", "https://images.unsplash.com/photo-1543076447-215ad9ba6923?w=300&q=80"),
            ProductData("Tenis Urban", "$210.000", "Comodidad y diseño moderno para el día a día.", "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=300&q=80"),
            ProductData("Gorra Sync", "$35.000", "Protección y estilo urbano con ajuste perfecto.", "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=300&q=80"),
            ProductData("Balón de Fútbol Pro", "$95.000", "Balón oficial con costuras reforzadas para alta durabilidad.", "https://images.unsplash.com/photo-1579952363873-27f3bade9f55?w=300&q=80"),
            ProductData("Raqueta de Tenis", "$210.000", "Raqueta ligera de grafito con encordado profesional.", "https://plus.unsplash.com/premium_photo-1666913667082-c1fecc45275d?w=300&q=80"),
            ProductData("Guantes de Gym", "$45.000", "Protección acolchada para entrenamiento con pesas.", "https://images.unsplash.com/photo-1557127972-1c446ea89ea5?w=300&q=80")
        )

        allProducts.forEach { prod ->
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 12)
                setBackgroundColor(Color.parseColor("#F2F4F7"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            }

            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    220
                ).apply { bottomMargin = 8 }
                scaleType = ImageView.ScaleType.CENTER_CROP
                loadUrl(prod.imageUrl)
                setOnClickListener {
                    showImageDialog(prod.imageUrl, prod.title)
                }
            }

            val titleViewItem = TextView(requireContext()).apply {
                text = prod.title
                typeface = Typeface.DEFAULT_BOLD
                textSize = 18f
                setTextColor(Color.parseColor("#000000"))
            }

            val priceView = TextView(requireContext()).apply {
                text = prod.price
                textSize = 16f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#D84F52"))
            }

            val descView = TextView(requireContext()).apply {
                text = prod.description
                textSize = 14f
                setTextColor(Color.parseColor("#333333"))
            }

            val addBtn = Button(requireContext()).apply {
                text = "🛒 Agregar al carrito"
                textSize = 12f
                setBackgroundColor(Color.parseColor("#D84F52"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    CartRepository.addItem(CartItem(prod.title, prod.price, prod.description, prod.imageUrl))
                    Toast.makeText(requireContext(), "¡${prod.title} agregado al carrito!", Toast.LENGTH_SHORT).show()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8
                }
            }

            val favBtn = Button(requireContext()).apply {
                text = "⭐ Agregar a favoritos"
                textSize = 12f
                setBackgroundColor(Color.parseColor("#2B8FA0"))
                setTextColor(Color.WHITE)
                setOnClickListener {
                    FavoritesRepository.addItem(FavoriteItem(prod.title, prod.price, prod.description, prod.imageUrl))
                    Toast.makeText(requireContext(), "¡${prod.title} agregado a Favoritos!", Toast.LENGTH_SHORT).show()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 6
                }
            }

            card.addView(imageView)
            card.addView(titleViewItem)
            card.addView(priceView)
            card.addView(descView)
            card.addView(addBtn)
            card.addView(favBtn)
            container.addView(card)
        }

        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private fun renderVideo() {
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(requireContext()).apply {
            text = "🎬 Videos y Reseñas de Productos"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        container.addView(title)

        // Reproductor de video activo
        val activeVideoTitle = TextView(requireContext()).apply {
            text = "Reproduciendo: Reseña Smartwatch Pro"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#D84F52"))
            setPadding(0, 0, 0, 8)
        }

        val webView = WebView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                420
            ).apply { bottomMargin = 16 }
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            webViewClient = WebViewClient()
        }
        this.webView = webView

        fun loadHtmlVideo(videoUrl: String, videoTitle: String) {
            activeVideoTitle.text = "Reproduciendo: $videoTitle"
            val html = """
                <html>
                <body style="margin:0;background:black;display:flex;justify-content:center;align-items:center;height:100vh;">
                    <video width="100%" height="100%" autoplay loop muted playsinline controls>
                        <source src="$videoUrl" type="video/mp4">
                        Tu navegador no soporta video HTML5.
                    </video>
                </body>
                </html>
            """.trimIndent()
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
        }

        loadHtmlVideo("https://v.ftcdn.net/09/08/13/27/700_F_908132772_dWNMIARe3SzIT5SZ4qEij7qh1ytNgmLs_ST.mp4", "Smartwatch Pro - Reseña")

        container.addView(activeVideoTitle)
        container.addView(webView)

        // Lista de videos organizados por producto
        val listTitle = TextView(requireContext()).apply {
            text = "Lista de reproducción"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 16, 0, 12)
        }
        container.addView(listTitle)

        data class ProductVideo(val productName: String, val videoTitle: String, val url: String, val badge: String)
        val productVideos = listOf(
            ProductVideo("Smartwatch Pro", "Reseña y funciones de entrenamiento", "https://v.ftcdn.net/09/08/13/27/700_F_908132772_dWNMIARe3SzIT5SZ4qEij7qh1ytNgmLs_ST.mp4", "Electrónica"),
            ProductVideo("Cafetera", "Tutorial de preparación de espresso", "https://v.ftcdn.net/01/99/94/53/700_F_199945383_Z7F6bH9SntILCslNPBk1poSr18jQArzZ_ST.mp4", "Hogar"),
            ProductVideo("Audífonos X9", "Prueba de sonido y diseño", "https://v.ftcdn.net/17/75/93/98/700_F_1775939845_piQa8vokxJ6SMND2YdNhJRF9WIRAdlvW_ST.mp4", "Electrónica"),
            ProductVideo("Laptop Aero", "Demostración de rendimiento", "https://v.ftcdn.net/04/66/14/30/700_F_466143069_YmW39PSSZrRSyBsi8POH8Zm8ZiOs3Uha_ST.mp4", "Electrónica")
        )

        productVideos.forEach { vid ->
            val card = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(12, 12, 12, 12)
                setBackgroundColor(Color.parseColor("#F2F4F7"))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 10
                }
                setOnClickListener {
                    loadHtmlVideo(vid.url, "${vid.productName} - ${vid.videoTitle}")
                    Toast.makeText(requireContext(), "Cargando: ${vid.productName}", Toast.LENGTH_SHORT).show()
                }
            }

            val badgeView = TextView(requireContext()).apply {
                text = "[${vid.badge}]"
                textSize = 11f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.parseColor("#D84F52"))
                setPadding(0, 0, 0, 2)
            }

            val prodNameView = TextView(requireContext()).apply {
                text = vid.productName
                typeface = Typeface.DEFAULT_BOLD
                textSize = 16f
                setTextColor(Color.parseColor("#000000"))
            }

            val descView = TextView(requireContext()).apply {
                text = vid.videoTitle
                textSize = 13f
                setTextColor(Color.parseColor("#555555"))
            }

            card.addView(badgeView)
            card.addView(prodNameView)
            card.addView(descView)
            container.addView(card)
        }

        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private fun renderWeb() {
        val urlInput = EditText(requireContext()).apply {
            hint = "https://www.google.com"
            setPadding(16, 16, 16, 16)
        }

        val loadButton = Button(requireContext()).apply {
            text = "Cargar página"
        }

        val web = WebView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            loadUrl("https://www.google.com/")
        }
        webView = web

        loadButton.setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isEmpty()) return@setOnClickListener
            val fullUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
                url
            } else {
                "https://$url"
            }
            web.loadUrl(fullUrl)
        }

        contentLayout.addView(urlInput)
        contentLayout.addView(loadButton)
        contentLayout.addView(web)
    }

    private fun renderCart() {
        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(requireContext()).apply {
            text = "🛒 Carrito de Compras"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        container.addView(title)

        val totalView = TextView(requireContext()).apply {
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#D84F52"))
            setPadding(0, 8, 0, 16)
        }

        val itemsContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }

        fun updateTotalPrice() {
            var total = 0
            CartRepository.items.filter { it.isSelected }.forEach { item ->
                val numericPrice = item.price.replace("$", "").replace(".", "").trim().toIntOrNull() ?: 0
                total += numericPrice
            }
            totalView.text = "Total seleccionado: $%,d".format(Locale("es", "CO"), total).replace(',', '.')
        }

        if (CartRepository.items.isEmpty()) {
            val emptyMsg = TextView(requireContext()).apply {
                text = "Tu carrito está vacío.\n\nExplora el Catálogo o las Categorías y agrega productos con el botón '🛒 Agregar al carrito'."
                textSize = 15f
                setTextColor(Color.parseColor("#666666"))
                setPadding(0, 8, 0, 16)
            }
            container.addView(emptyMsg)
            totalView.visibility = View.GONE
        } else {
            totalView.visibility = View.VISIBLE
            updateTotalPrice()
            container.addView(totalView)

            CartRepository.items.forEach { cartItem ->
                val card = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(12, 12, 12, 12)
                    gravity = Gravity.CENTER_VERTICAL
                    setBackgroundColor(Color.parseColor("#F2F4F7"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 10
                    }
                }

                val checkBox = CheckBox(requireContext()).apply {
                    isChecked = cartItem.isSelected
                    setOnCheckedChangeListener { _, isChecked ->
                        cartItem.isSelected = isChecked
                        updateTotalPrice()
                    }
                }

                val itemImg = ImageView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(75, 75).apply {
                        marginEnd = 8
                        marginStart = 4
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    loadUrl(cartItem.imageUrl)
                    setOnClickListener {
                        showImageDialog(cartItem.imageUrl, cartItem.title)
                    }
                }

                val info = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(4, 0, 4, 0)
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                val nameView = TextView(requireContext()).apply {
                    text = cartItem.title
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 16f
                    setTextColor(Color.parseColor("#000000"))
                }

                val priceView = TextView(requireContext()).apply {
                    text = "${cartItem.price} - ${cartItem.description}"
                    textSize = 13f
                    setTextColor(Color.parseColor("#555555"))
                    maxLines = 2
                }

                info.addView(nameView)
                info.addView(priceView)

                val deleteBtn = Button(requireContext()).apply {
                    text = "🗑️"
                    textSize = 14f
                    setBackgroundColor(Color.TRANSPARENT)
                    setOnClickListener {
                        CartRepository.removeItem(cartItem)
                        Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                        showOption("Carrito")
                    }
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                card.addView(checkBox)
                card.addView(itemImg)
                card.addView(info)
                card.addView(deleteBtn)
                itemsContainer.addView(card)
            }

            container.addView(itemsContainer)
        }

        // Acciones rápidas
        val actionsTitle = TextView(requireContext()).apply {
            text = "Acciones rápidas"
            textSize = 18f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 24, 0, 8)
        }
        container.addView(actionsTitle)

        val statusText = TextView(requireContext()).apply {
            text = "Selecciona productos y elige una acción"
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
            setPadding(0, 8, 0, 12)
        }

        val buyButton = Button(requireContext()).apply {
            text = "Comprar ahora"
            setBackgroundColor(Color.parseColor("#D84F52"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                val selectedItems = CartRepository.items.filter { it.isSelected }
                if (CartRepository.items.isEmpty()) {
                    statusText.text = "El carrito está vacío. Agrega productos para comprar."
                } else if (selectedItems.isNotEmpty()) {
                    showPaymentDialog(selectedItems)
                } else {
                    statusText.text = "Por favor selecciona al menos un producto para comprar."
                }
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }

        val saveButton = Button(requireContext()).apply {
            text = "Guardar"
            setBackgroundColor(Color.parseColor("#2B8FA0"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                val selectedItems = CartRepository.items.filter { it.isSelected }
                if (selectedItems.isNotEmpty()) {
                    selectedItems.forEach { cartItem ->
                        FavoritesRepository.addItem(FavoriteItem(cartItem.title, cartItem.price, cartItem.description, cartItem.imageUrl))
                    }
                    statusText.text = "¡${selectedItems.size} producto(s) guardado(s) en Favoritos!"
                    Toast.makeText(requireContext(), "¡Guardado en Favoritos!", Toast.LENGTH_SHORT).show()
                } else {
                    statusText.text = "Selecciona al menos un producto para guardar."
                }
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }

        val shareButton = Button(requireContext()).apply {
            text = "Compartir"
            setBackgroundColor(Color.parseColor("#F0B38A"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                val selectedItems = CartRepository.items.filter { it.isSelected }
                val shareText = if (selectedItems.isNotEmpty()) {
                    "¡Mira mis productos seleccionados en StockSync:\n" +
                        selectedItems.joinToString("\n") { "- ${it.title} (${it.price})" }
                } else {
                    "¡Visita StockSync y descubre nuestros productos!"
                }
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                try {
                    context?.startActivity(Intent.createChooser(intent, "Compartir vía"))
                } catch (e: Exception) {
                    statusText.text = "Enlace compartido"
                }
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }

        val switchWidget = SwitchCompat(requireContext()).apply {
            text = "Notificaciones activadas"
            isChecked = true
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 8, 0, 16)
        }

        container.addView(buyButton)
        container.addView(saveButton)
        container.addView(shareButton)
        container.addView(switchWidget)
        container.addView(statusText)

        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private fun renderFavorites() {
        if (!::contentLayout.isInitialized) return
        contentLayout.removeAllViews()

        val scrollView = ScrollView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        val title = TextView(requireContext()).apply {
            text = "⭐ Mis Favoritos"
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        container.addView(title)

        if (FavoritesRepository.items.isEmpty()) {
            val emptyMsg = TextView(requireContext()).apply {
                text = "No tienes productos guardados en favoritos.\n\nVe al Catálogo o Categorías, y presiona '⭐ Agregar a favoritos'."
                textSize = 15f
                setTextColor(Color.parseColor("#666666"))
                setPadding(0, 8, 0, 16)
            }
            container.addView(emptyMsg)
        } else {
            val itemsContainer = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
            }

            FavoritesRepository.items.forEach { favItem ->
                val card = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(12, 12, 12, 12)
                    gravity = Gravity.CENTER_VERTICAL
                    setBackgroundColor(Color.parseColor("#F2F4F7"))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 10
                    }
                }

                val favImg = ImageView(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(75, 75).apply {
                        marginEnd = 8
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    loadUrl(favItem.imageUrl)
                    setOnClickListener {
                        showImageDialog(favItem.imageUrl, favItem.title)
                    }
                }

                val info = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(4, 0, 4, 0)
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                val nameView = TextView(requireContext()).apply {
                    text = favItem.title
                    typeface = Typeface.DEFAULT_BOLD
                    textSize = 16f
                    setTextColor(Color.parseColor("#000000"))
                }

                val priceView = TextView(requireContext()).apply {
                    text = "${favItem.price} - ${favItem.description}"
                    textSize = 13f
                    setTextColor(Color.parseColor("#555555"))
                    maxLines = 2
                }

                info.addView(nameView)
                info.addView(priceView)

                val deleteBtn = Button(requireContext()).apply {
                    text = "🗑️"
                    textSize = 14f
                    setBackgroundColor(Color.TRANSPARENT)
                    setOnClickListener {
                        FavoritesRepository.removeItem(favItem)
                        Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                        contentLayout.post {
                            renderFavorites()
                        }
                    }
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                card.addView(favImg)
                card.addView(info)
                card.addView(deleteBtn)
                itemsContainer.addView(card)
            }

            container.addView(itemsContainer)
        }

        scrollView.addView(container)
        contentLayout.addView(scrollView)
    }

    private fun showPaymentDialog(selectedItems: List<CartItem>) {
        val dialog = Dialog(requireContext())
        val scrollView = ScrollView(requireContext())
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FFFFFF"))
            setPadding(24, 24, 24, 24)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val title = TextView(requireContext()).apply {
            text = "💳 Pasarela de Pago"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.parseColor("#000000"))
            setPadding(0, 0, 0, 16)
        }
        layout.addView(title)

        // Tipo de Tarjeta
        val typeLabel = TextView(requireContext()).apply {
            text = "Tipo de Tarjeta:"
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
        }
        layout.addView(typeLabel)

        val cardTypeGroup = RadioGroup(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 4, 0, 12)
        }
        val creditRadio = RadioButton(requireContext()).apply {
            id = View.generateViewId()
            text = "Crédito"
            isChecked = true
            setTextColor(Color.parseColor("#000000"))
        }
        val debitRadio = RadioButton(requireContext()).apply {
            id = View.generateViewId()
            text = "Débito / PSE"
            setTextColor(Color.parseColor("#000000"))
        }
        cardTypeGroup.addView(creditRadio)
        cardTypeGroup.addView(debitRadio)
        layout.addView(cardTypeGroup)

        // Franquicia / Banco
        val franchiseLabel = TextView(requireContext()).apply {
            text = "Franquicia / Banco:"
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
        }
        layout.addView(franchiseLabel)

        val franchiseSpinner = Spinner(requireContext()).apply {
            val franchises = arrayOf("Visa", "Mastercard", "American Express", "Diners Club", "Bancolombia (PSE)", "Davivienda (PSE)", "Nequi", "BBVA", "Banco de Bogotá")
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, franchises)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 12 }
        }
        layout.addView(franchiseSpinner)

        // Número de Tarjeta
        val numInput = EditText(requireContext()).apply {
            hint = "Número de Tarjeta / Cuenta"
            inputType = InputType.TYPE_CLASS_NUMBER
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }
        layout.addView(numInput)

        // Expiración y CVV
        val expInput = EditText(requireContext()).apply {
            hint = "MM/AA"
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val cvvInput = EditText(requireContext()).apply {
            hint = "CVV"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }
        rowLayout.addView(expInput)
        rowLayout.addView(cvvInput)
        layout.addView(rowLayout)

        // Nombre del Titular
        val nameInput = EditText(requireContext()).apply {
            hint = "Nombre del Titular"
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }
        layout.addView(nameInput)

        // Cédula
        val idInput = EditText(requireContext()).apply {
            hint = "Cédula de Ciudadanía"
            inputType = InputType.TYPE_CLASS_NUMBER
            setPadding(16, 16, 16, 16)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }
        layout.addView(idInput)

        // Botón Pagar
        val payBtn = Button(requireContext()).apply {
            text = "Confirmar y Pagar"
            setBackgroundColor(Color.parseColor("#D84F52"))
            setTextColor(Color.WHITE)
            setOnClickListener {
                if (numInput.text.toString().isNotEmpty() && nameInput.text.toString().isNotEmpty()) {
                    Toast.makeText(requireContext(), "¡Pago exitoso de ${selectedItems.size} producto(s)!", Toast.LENGTH_LONG).show()
                    CartRepository.clearSelected()
                    dialog.dismiss()
                    showOption("Carrito")
                } else {
                    Toast.makeText(requireContext(), "Por favor completa los datos de pago", Toast.LENGTH_SHORT).show()
                }
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 8 }
        }
        layout.addView(payBtn)

        val cancelBtn = Button(requireContext()).apply {
            text = "Cancelar"
            setBackgroundColor(Color.parseColor("#777777"))
            setTextColor(Color.WHITE)
            setOnClickListener { dialog.dismiss() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        layout.addView(cancelBtn)

        scrollView.addView(layout)
        dialog.setContentView(scrollView)
        dialog.show()
    }
}
