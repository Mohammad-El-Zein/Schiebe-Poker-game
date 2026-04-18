# 🃏 Schiebe-Poker – Card Game

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-1.9-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Gradle-8.x-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle"/>
  <img src="https://img.shields.io/badge/BoardGameWork-Framework-4CAF50?style=for-the-badge" alt="BGW"/>
  <img src="https://img.shields.io/badge/Hotseat-Multiplayer-FF6B6B?style=for-the-badge" alt="Hotseat"/>
</p>

<p align="center">
  <strong>Eine Kotlin-Implementierung des Kartenspiels „Schiebe-Poker"  mit Hotseat-Modus, Spielprotokoll und Poker-Wertung.</strong>
</p>

---

## 🚀 Schnellstart

<table>
<tr>
<td>

### ⬇️ [Download Schiebe-Poker Game](https://github.com/Mohammad-El-Zein/Schiebe-Poker-game/releases/latest)

</td>
</tr>
</table>

1. **Download:** Klicke auf den Link oben und lade `Play-SchiebePoker.zip` herunter
2. **Entpacken:** Entpacke die ZIP-Datei
3. **Starten:** Gehe zu `distribution/bin/` und doppelklicke auf:
   - **Windows:** `Projekt1.bat`
   - **Mac/Linux:** `Projekt1`
4. **Spielen!** 🎮

> 💡 *Keine Installation nötig – einfach entpacken und spielen!*
---

## 📋 Über das Projekt

**Schiebe-Poker** ist ein Kartenspiel für 2–4 Spieler mit einem Standard-Blatt aus 52 Karten:

> {Kreuz, Pik, Herz, Karo} × {2, 3, 4, 5, 6, 7, 8, 9, 10, Bube, Dame, König, Ass}

Jede*r Spieler*in erhält zu Beginn **2 verdeckte** und **3 offene** Karten. Drei Karten liegen offen in der Mitte. Die Spieler*innen einigen sich vor Spielstart auf **2–7 Runden**.

---

## ✨ Features

|         Feature       |     Beschreibung        |
|-----------------------|--------------------------|
| 👥 **Hotseat-Modus** | Alle Spieler*innen spielen am gleichen Bildschirm |
| 🙈 **Privatsphäre-Screen** | Verdeckt alle Karten zwischen den Zügen |
| 📋 **Spielprotokoll** | Alle Aktionen werden live protokolliert |
| 🏆 **Poker-Wertung** | Handstärke von Höchste Karte bis Royal Flush |
| ✏️ **Namenskonfiguration** | Spielernamen frei wählbar beim Start |
| 📊 **Abschluss-Ranking** | Rangfolge aller Spieler*innen am Spielende |

---

## 🎮 Spielablauf

Pro Zug führt jede*r Spieler*in **genau zwei Aktionen** aus (beliebig kombinierbar):

### Aktion 1 – Schieben
| Richtung | Beschreibung |
|----------|-------------|
| ⬅️ **Links** | Linke Karte auf Ablagestapel, Karten rücken nach links, neue Karte von rechts |
| ➡️ **Rechts** | Rechte Karte auf Ablagestapel, Karten rücken nach rechts, neue Karte von links |

### Aktion 2 – Tauschen
| Option | Beschreibung |
|--------|-------------|
| 1️⃣ **Einzeltausch** | Eine eigene offene Karte mit einer Karte aus der Mitte tauschen |
| 3️⃣ **Dreifachtausch** | Alle drei offenen Karten mit den drei Karten in der Mitte tauschen |
| ⏭️ **Passen** | Keinen Tausch durchführen |

> 💡 *Ist der Nachziehstapel leer, wird der Ablagestapel gemischt und als neuer Nachziehstapel verwendet.*

---

## 🏆 Punktewertung

Die Hand jedes Spielenden besteht aus **2 verdeckten + 3 offenen Karten** und wird nach Standard-**Poker-Regeln** gewertet:

```
Royal Flush  >  Straight Flush  >  Vierling  >  Full House  >  Flush >  Straße  >  Drilling  >  Zwei Paare  >  Paar  >  Höchste Karte
```
---

## 🛠️ Tech Stack

```
Kotlin 1.9  •  Gradle  •  BoardGameWork (BGW)  •  JUnit 5
```

---

## 📁 Projektstruktur

```
src/
├── main/kotlin/
│   ├── entity/          # Spielobjekte (Card, Player, GameState)
│   ├── service/         # Spiellogik & Regelprüfung
│   └── gui/             # UI-Komponenten (BGW)
└── test/kotlin/         # Unit Tests
```


## 👨‍💻 Autor

<table>
  <tr>
    <td align="center">
      <strong>Mohammad El Zein</strong><br>
      Informatik Student @ TU Dortmund<br><br>
      <a href="https://github.com/Mohammad-El-Zein">
        <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/>
      </a>
    </td>
  </tr>
</table>

> 🎓 *Dieses Projekt wurde im Rahmen des Softwarepraktikums (SoPra) an der TU Dortmund entwickelt.*

---

## 📄 Lizenz

Dieses Projekt wurde für Bildungszwecke erstellt.

---
