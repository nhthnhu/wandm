package com.wandm.models

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

data class Menu(val icon: MaterialDrawableBuilder.IconValue, val content: String, val isSelected: Boolean, val color: Int)