package com.one.task.domain

object InitialData {
    fun getGettingStartedBlocks(): List<ContentBlock> {
        return listOf(
            ImageBlock(generateUuid(), 0, localPath = "", url = "https://images.unsplash.com/photo-1484480974693-6ca0a78fb36b?q=80&w=1172&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D", caption = "A beautiful landscape to inspire you", sizeMode = "Large", showCaption = true),
            HeadingBlock(generateUuid(), 1, level = 1, text = "Hello and Welcome!"),
            TextBlock(generateUuid(), 2, "OneTask is a flexible, block-based workspace designed to help you organize everything. Here is a quick tour of what you can do:"),
            DividerBlock(generateUuid(), 3),
            HeadingBlock(generateUuid(), 4, level = 2, text = "Rich Text Formatting"),
            TextBlock(generateUuid(), 5, "You can easily format your text using the toolbar at the bottom. Try **bolding**, *italicizing*, or adding an __underline__ to your thoughts."),
            HeadingBlock(generateUuid(), 6, level = 2, text = "Task Management"),
            CheckboxBlock(generateUuid(), 7, "Plan your week", isChecked = true, tag = "planning"),
            CheckboxBlock(generateUuid(), 8, "Write the project proposal", isChecked = false, tag = "work"),
            DividerBlock(generateUuid(), 9),
            HeadingBlock(generateUuid(), 10, level = 2, text = "Data Tables"),
            TextBlock(generateUuid(), 11, "You can also organize data in tables! Here is a fun example:"),
            TableBlock(
                generateUuid(), 12, "Fastest Animals (Source: Wikipedia)", 4, 3,
                listOf(
                    listOf("Animal", "Max Speed", "Class"),
                    listOf("Peregrine Falcon", "389 km/h (242 mph)", "Bird"),
                    listOf("Cheetah", "109 km/h (68 mph)", "Mammal"),
                    listOf("Black Marlin", "129 km/h (80 mph)", "Fish")
                )
            )
        )
    }
}
