package com.aviad.chordstv.data.source

import com.aviad.chordstv.domain.model.Language
import com.aviad.chordstv.domain.model.Song

/**
 * Built-in sample catalogue.
 *
 * NOTE: All lyrics here are ORIGINAL placeholder texts written for this demo
 * so the project ships without copyrighted material. Replace this file (or the
 * repository behind it) with your own licensed song source.
 */
object SampleSongs {

    val all: List<Song> = listOf(

        Song(
            id = "he-001",
            title = "אור בחלון",
            artist = "להקת הדוגמה",
            language = Language.HEBREW,
            originalKey = "Am",
            tags = listOf("שקט", "אקוסטי"),
            body = """
                #בית 1
                [Am]ערב יורד על ה[G]עיר הקטנה
                [F]אור בחלון מח[E]כה לי
                [Am]רוח נושבת בין [G]עצי השדרה
                [F]ואני שוב [E]הולך אליך

                #פזמון
                [C]בואי נש[G]ב על המרפסת
                [Am]נספור כו[F]כבים בשקט
                [C]הלילה [G]ארוך ואין [Am]מה למהר [F]
                [C]רק את [G]ואני ואור [Am]בחלון [E]

                #בית 2
                [Am]הכביש ריק וה[G]שמיים סגולים
                [F]הפנס מהב[E]הב לאט
                [Am]כל השירים שלא [G]אמרנו במילים
                [F]מחכים לנו [E]עוד רגע אחד
            """.trimIndent()
        ),

        Song(
            id = "he-002",
            title = "דרך הביתה",
            artist = "יובל דמו",
            language = Language.HEBREW,
            originalKey = "G",
            tags = listOf("פולק"),
            body = """
                #בית
                [G]יצאתי בבוקר עם [D]תיק על הגב
                [Em]מפה ישנה ו[C]לב קצת רועד
                [G]הדרך ארוכה, ה[D]שמש בצד
                [Em]אבל כל צעד מק[C]רב אותי

                #פזמון
                [C]דרך ה[G]ביתה, דרך ה[D]ביתה
                [C]כל השבי[G]לים מובילים ל[D]שם
                [C]דרך ה[G]ביתה, אין [Em]לי כתובת
                [C]רק את הריח של [D]הים

                #גשר
                [Am]ואם אתעה [Em]בלילה
                [C]הכוכבים יזכירו [D]לי
            """.trimIndent()
        ),

        Song(
            id = "he-003",
            title = "קפה של שבת",
            artist = "רונית ניסיון",
            language = Language.HEBREW,
            originalKey = "D",
            tags = listOf("פופ", "בוקר"),
            body = """
                #בית 1
                [D]שבת בבוקר, [A]הרדיו מתעורר
                [Bm]ריח של קפה [G]ממלא את הבית
                [D]העיתון על השול[A]חן, אף אחד לא ממהר
                [Bm]והזמן כמו [G]דבש נשפך לאט

                #פזמון
                [G]תישאר עוד [A]קצת
                [D]אל תלך [Bm]עדיין
                [G]יש לנו את כל ה[A]בוקר
                [D]ואת כל היום [G]

                #בית 2
                [D]הילדים יש[A]נים, החלון פתוח
                [Bm]ציפור אחת [G]שרה בלי מילים
                [D]אני מוזג לך [A]עוד כוס אחת
                [Bm]ומחייך כי [G]הכל בסדר
            """.trimIndent()
        ),

        Song(
            id = "en-001",
            title = "Paper Boats",
            artist = "The Demo Collective",
            language = Language.ENGLISH,
            originalKey = "C",
            tags = listOf("folk", "acoustic"),
            body = """
                #Verse 1
                [C]Down by the river where the [G]willows lean
                [Am]We folded paper boats and [F]set them free
                [C]You said the current knows the [G]way to go
                [Am]And I believed you like I'd [F]never know

                #Chorus
                [F]Sail on, [G]sail on, little [C]paper boat
                [F]Carry all the [G]words I never [Am]wrote
                [F]If you reach the [G]sea before the [C]morning [Am]light
                [F]Tell them that we [G]tried to get it [C]right

                #Verse 2
                [C]The summer burned the edges of the [G]afternoon
                [Am]We hummed a tune we'd learned a [F]bit too soon
                [C]Now every time it rains I [G]watch the stream
                [Am]And wonder if it still [F]remembers me
            """.trimIndent()
        ),

        Song(
            id = "en-002",
            title = "Neon Rain",
            artist = "Ella Placeholder",
            language = Language.ENGLISH,
            originalKey = "Em",
            tags = listOf("pop", "night"),
            body = """
                #Verse 1
                [Em]City lights are [C]bleeding on the [G]window [D]pane
                [Em]Every taxi [C]passing sings a [G]different [D]name
                [Em]I keep walking [C]nowhere just to [G]hear the [D]sound
                [Em]Of the neon [C]rain that's falling [G]all a[D]round

                #Chorus
                [C]Hold me [D]under the [Em]neon rain
                [C]Say my [D]name until it [G]feels the same
                [C]Nothing [D]here is ever [Em]what it seems
                [C]But the [D]rain is real, and [G]so are [D]dreams

                #Bridge
                [Am]Midnight's just a [Bm]number on a clock
                [C]We could make it [D]stop, we could make it [Em]stop
            """.trimIndent()
        ),

        Song(
            id = "en-003",
            title = "Slow Sunday",
            artist = "The Demo Collective",
            language = Language.ENGLISH,
            originalKey = "A",
            tags = listOf("country", "easy"),
            body = """
                #Verse 1
                [A]Coffee's on the [D]stove, the dog's a[A]sleep
                [E]Sunlight on the [A]porch is running [E]deep
                [A]No place to be, no [D]calls to make to[A]day
                [E]Just a slow, slow [A]Sunday drifting a[E]way

                #Chorus
                [D]Let it [A]roll, let it [E]roll
                [D]Nothing's [A]pulling on my [E]soul
                [D]Let the [A]hours come and [F#m]go
                [D]On a slow, slow [E]Sunday [A]morning
            """.trimIndent()
        )
    )
}
