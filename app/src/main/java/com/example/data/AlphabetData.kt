package com.example.data

data class AlphabetItem(
    val letter: Char,
    val word: String,
    val emoji: String,
    val phonicsSound: String,
    val colorHex: Long,
    val funFact: String,
    val exampleWords: List<String>
)

object AlphabetRepository {
    val alphabetList: List<AlphabetItem> = listOf(
        AlphabetItem(
            letter = 'A',
            word = "Apple",
            emoji = "🍎",
            phonicsSound = "A says /æ/! A is for Apple!",
            colorHex = 0xFFFF5964,
            funFact = "Apples can float in water because they have air inside!",
            exampleWords = listOf("Ant 🐜", "Airplane ✈️")
        ),
        AlphabetItem(
            letter = 'B',
            word = "Butterfly",
            emoji = "🦋",
            phonicsSound = "B says /b/! B is for Butterfly!",
            colorHex = 0xFF4CC9F0,
            funFact = "Butterflies taste with their tiny feet!",
            exampleWords = listOf("Ball ⚽", "Bear 🐻")
        ),
        AlphabetItem(
            letter = 'C',
            word = "Cat",
            emoji = "🐱",
            phonicsSound = "C says /k/! C is for Cat!",
            colorHex = 0xFFFF9F1C,
            funFact = "Cats can jump up to six times their height!",
            exampleWords = listOf("Car 🚗", "Cake 🎂")
        ),
        AlphabetItem(
            letter = 'D',
            word = "Dog",
            emoji = "🐶",
            phonicsSound = "D says /d/! D is for Dog!",
            colorHex = 0xFF06D6A0,
            funFact = "Dogs have an amazing sense of smell and wag their tails when happy!",
            exampleWords = listOf("Duck 🦆", "Dolphin 🐬")
        ),
        AlphabetItem(
            letter = 'E',
            word = "Elephant",
            emoji = "🐘",
            phonicsSound = "E says /e/! E is for Elephant!",
            colorHex = 0xFF8338EC,
            funFact = "Elephants are the biggest animals that walk on land!",
            exampleWords = listOf("Egg 🥚", "Eagle 🦅")
        ),
        AlphabetItem(
            letter = 'F',
            word = "Frog",
            emoji = "🐸",
            phonicsSound = "F says /f/! F is for Frog!",
            colorHex = 0xFF2EC4B6,
            funFact = "Frogs drink water right through their skin!",
            exampleWords = listOf("Fish 🐟", "Flower 🌸")
        ),
        AlphabetItem(
            letter = 'G',
            word = "Giraffe",
            emoji = "🦒",
            phonicsSound = "G says /g/! G is for Giraffe!",
            colorHex = 0xFFFFB703,
            funFact = "Giraffes have super long necks to eat tall tree leaves!",
            exampleWords = listOf("Grapes 🍇", "Guitar 🎸")
        ),
        AlphabetItem(
            letter = 'H',
            word = "Horse",
            emoji = "🐴",
            phonicsSound = "H says /h/! H is for Horse!",
            colorHex = 0xFFFB8500,
            funFact = "Horses can sleep both lying down and standing up!",
            exampleWords = listOf("Hat 🎩", "House 🏠")
        ),
        AlphabetItem(
            letter = 'I',
            word = "Ice Cream",
            emoji = "🍦",
            phonicsSound = "I says /ɪ/! I is for Ice Cream!",
            colorHex = 0xFFFF70A6,
            funFact = "Ice cream is a delicious cool treat enjoyed worldwide!",
            exampleWords = listOf("Iguana 🦎", "Island 🏝️")
        ),
        AlphabetItem(
            letter = 'J',
            word = "Jellyfish",
            emoji = "🪼",
            phonicsSound = "J says /dʒ/! J is for Jellyfish!",
            colorHex = 0xFF3A86FF,
            funFact = "Jellyfish have been swimming in oceans since before dinosaurs!",
            exampleWords = listOf("Juice 🧃", "Jungle 🌴")
        ),
        AlphabetItem(
            letter = 'K',
            word = "Kangaroo",
            emoji = "🦘",
            phonicsSound = "K says /k/! K is for Kangaroo!",
            colorHex = 0xFFE07A5F,
            funFact = "Mother kangaroos carry their cute joeys in a front pouch!",
            exampleWords = listOf("Kite 🪁", "Koala 🐨")
        ),
        AlphabetItem(
            letter = 'L',
            word = "Lion",
            emoji = "🦁",
            phonicsSound = "L says /l/! L is for Lion!",
            colorHex = 0xFFFFBE0B,
            funFact = "Lions are called the King of the Jungle and have big golden manes!",
            exampleWords = listOf("Lemon 🍋", "Leaf 🍃")
        ),
        AlphabetItem(
            letter = 'M',
            word = "Monkey",
            emoji = "🐵",
            phonicsSound = "M says /m/! M is for Monkey!",
            colorHex = 0xFF9381FF,
            funFact = "Monkeys love swinging through trees and peeling sweet bananas!",
            exampleWords = listOf("Moon 🌙", "Mango 🥭")
        ),
        AlphabetItem(
            letter = 'N',
            word = "Nest",
            emoji = "🪺",
            phonicsSound = "N says /n/! N is for Nest!",
            colorHex = 0xFF05B292,
            funFact = "Birds make cozy nests with twigs and leaves to protect their eggs!",
            exampleWords = listOf("Nut 🥜", "Night 🌃")
        ),
        AlphabetItem(
            letter = 'O',
            word = "Owl",
            emoji = "🦉",
            phonicsSound = "O says /ɒ/! O is for Owl!",
            colorHex = 0xFFE76F51,
            funFact = "Owls can rotate their heads almost all the way around!",
            exampleWords = listOf("Orange 🍊", "Octopus 🐙")
        ),
        AlphabetItem(
            letter = 'P',
            word = "Penguin",
            emoji = "🐧",
            phonicsSound = "P says /p/! P is for Penguin!",
            colorHex = 0xFF2A9D8F,
            funFact = "Penguins slide on their bellies across icy snow hills!",
            exampleWords = listOf("Panda 🐼", "Pizza 🍕")
        ),
        AlphabetItem(
            letter = 'Q',
            word = "Queen",
            emoji = "👑",
            phonicsSound = "Q says /kw/! Q is for Queen!",
            colorHex = 0xFF7209B7,
            funFact = "Queens wear sparkling royal crowns full of gemstones!",
            exampleWords = listOf("Quilt 🛏️", "Quail 🐦")
        ),
        AlphabetItem(
            letter = 'R',
            word = "Rainbow",
            emoji = "🌈",
            phonicsSound = "R says /r/! R is for Rainbow!",
            colorHex = 0xFFFF006E,
            funFact = "Rainbows appear in the sky when sunshine shines through raindrops!",
            exampleWords = listOf("Rabbit 🐰", "Rocket 🚀")
        ),
        AlphabetItem(
            letter = 'S',
            word = "Sun",
            emoji = "☀️",
            phonicsSound = "S says /s/! S is for Sun!",
            colorHex = 0xFFFFD166,
            funFact = "The Sun gives warmth and light to all living things on Earth!",
            exampleWords = listOf("Star ⭐", "Strawberry 🍓")
        ),
        AlphabetItem(
            letter = 'T',
            word = "Tiger",
            emoji = "🐯",
            phonicsSound = "T says /t/! T is for Tiger!",
            colorHex = 0xFFFF6F00,
            funFact = "Every tiger has unique orange and black stripes, just like fingerprints!",
            exampleWords = listOf("Tree 🌳", "Train 🚂")
        ),
        AlphabetItem(
            letter = 'U',
            word = "Umbrella",
            emoji = "☂️",
            phonicsSound = "U says /ʌ/! U is for Umbrella!",
            colorHex = 0xFF4361EE,
            funFact = "Umbrellas keep us cozy and dry on rainy puddle days!",
            exampleWords = listOf("Unicorn 🦄", "Up ⬆️")
        ),
        AlphabetItem(
            letter = 'V',
            word = "Violin",
            emoji = "🎻",
            phonicsSound = "V says /v/! V is for Violin!",
            colorHex = 0xFF9D4EDD,
            funFact = "A violin produces beautiful musical melodies with gentle bow strokes!",
            exampleWords = listOf("Volcano 🌋", "Vegetable 🥕")
        ),
        AlphabetItem(
            letter = 'W',
            word = "Whale",
            emoji = "🐋",
            phonicsSound = "W says /w/! W is for Whale!",
            colorHex = 0xFF0077B6,
            funFact = "Blue whales are the largest creatures that ever lived on Earth!",
            exampleWords = listOf("Watermelon 🍉", "Windmill 🌬️")
        ),
        AlphabetItem(
            letter = 'X',
            word = "Xylophone",
            emoji = "🎶",
            phonicsSound = "X says /ks/! X is for Xylophone!",
            colorHex = 0xFFF72585,
            funFact = "A xylophone makes joyful ding-dong tunes when hit with wooden mallets!",
            exampleWords = listOf("X-ray 🩻", "Xenon 💡")
        ),
        AlphabetItem(
            letter = 'Y',
            word = "Yak",
            emoji = "🐂",
            phonicsSound = "Y says /j/! Y is for Yak!",
            colorHex = 0xFF6D6875,
            funFact = "Yaks have long shaggy fur coats to stay warm in snowy mountains!",
            exampleWords = listOf("Yo-yo 🪀", "Yogurt 🥣")
        ),
        AlphabetItem(
            letter = 'Z',
            word = "Zebra",
            emoji = "🦓",
            phonicsSound = "Z says /z/! Z is for Zebra!",
            colorHex = 0xFF3D348B,
            funFact = "Zebras communicate with their ears and love running in friendly herds!",
            exampleWords = listOf("Zoo 🦁", "Zipper 🤐")
        )
    )

    fun getByLetter(char: Char): AlphabetItem? {
        return alphabetList.find { it.letter.equals(char, ignoreCase = true) }
    }
}
