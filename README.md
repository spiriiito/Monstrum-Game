# 🐉 Monstrum - Java Monster Battle Game
Monstrum is a Java-based monster battle game showcasing Object-Oriented Programming concepts with multiple game modes, special attacks, weather effects, perks, and GUI created with Java Swing.
Each monster belongs to a specific elemental type—Fire, Water, Thunder, Dark, or Acid—each with unique strengths and special abilities. The player navigates through an intuitive UI to choose monsters, select battle modes, assign weapons, and fight strategically across themed arenas.

## Features

- 🎮 **Multiple Game Modes**
  - **PvAI** - Player vs AI battle
  - **PvP** – Player vs Player battles  
  - **Hardcore** – Tougher rules and restrictions  
  - **Survival** – Endurance mode with perks
  - **Random** - Randomized settings, teams, arena, weapons, etc

- 🐲 **Monsters & Abilities**  
  - Unique monsters with basic and **special attacks**  
  - Special cooldown system 

- 🌦 **Dynamic Effects**  
  - Weather & arena effects influence battles  
  - Survival perks & weapons change the tide of combat  

- 🎵 **Sound Effects**  
  - Monster specials and deaths include immersive sounds  

- 🖥 **GUI**  
  - Interactive Java-based UI with logs, icons, and win/lose popups  

## Installation and setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/spiriiito/Monstrum-Game.git
   cd Monstrum-Game
  
2. **Open in IntelliJ IDEA or any other Java IDE**
  - If IntelliJ asks for a JDK, select JDK 17+ (you may need to install it first)
  - You do not need Maven or Gradle to run this project — it works as a plain Java project.
3. **Run the game**
  - Navigate to src -> monstrum -> game -> UI -> GameUI and run GameUI.java.
  - Follow the guided UI screens to complete setup
  - P.S. There is also a fully playable console version of the game which can be launched by configuring the desired settings in Main.java file and running Main.java . Main.java is located inside the game folder.

## Game modes and steps
1. **🤖 Player vs AI:**
  - Choose team size (1–5 monsters)
  - Select your team
  - Select weapon and arena
  - Assign weapon to one monster
  - Fight against AI-controlled team turn by turn
2. **🤼‍♂️ Player vs Player:**
  - Both players take turns choosing their teams
  - Select shared weapons and arenas
  - Equip weapon to a monster in each team
  - Play turn-by-turn from the same screen
3. **🏹 Survival Mode:**
  - Choose 1 monster
  - Pick one weapon and assign it
  - Choose a Survival Perk (e.g., Adrenaline Rush: +5 energy per turn)
  - Battle endless AI teams in waves
  - After each wave: +20 HP and +30 energy
4. **💀 Hardcore Mode:**
  - Similar to PvAI setup
  - No potions allowed
  - Weather effects enabled (e.g., Burn, Energy Drain)
  - AI has stronger damage
5. **🎲 Random Mode:**
  - All settings, teams, weapons, weather effects and arena are randomized
  - A surprise challenge every time

## Monster types

- 🔥 Fire Monster: Scorches enemies with intense fire, dealing high damage. Standard HP and energy levels, focuses on raw damage.
- 💧 Water Monster: Unleashes a powerful water-based strike. Balanced stats with enhanced power in water environments (Ocean Arena).
- ⚡ Thunder Monster: Lightning-based attack that deals heavy shock damage. Higher base HP than other monsters; energy-efficient in special moves.
- 🌑 Dark Monster: Drains life from enemies. Unique life steal effect – heals 15 HP after every successful special attack.
- 🧪 Acid Monster: Poisons enemies over multiple turns. Poison damage: default 5/turn for 2 turns.

## Arena effects

- 🌋 Volcano: Fire monsters get +20 HP
- 🌊 Ocean: Water monsters’ special attacks deal +7 damage
- ⛰️ Zeus Mountain: Thunder monsters’ special attack cost is reduced
- 🌲 Dark Forest: Dark monsters heal more after specials
- 🐸 Swamp: Acid monsters apply stronger poison

## Weapon effects

- 🔨 Hammer: Adds +8 damage, but drains -2 energy at the end of each turn
- 🔱 Trident: Adds +5 to basic attack
- 🛡️ Shield: Blocks 5 damage 
- Only one monster in the team gets the weapon

## Weather effects

- 🌧️ Rain: All monsters lose -5 energy per turn
- ⛈️ Storm: 10% chance for each attack to backfire (deal 5 damage to attacker)
- 🌫️ Fog: 25% chance to miss basic attacks
- ☀️ Heatwave: Every 2 turns, monsters take 3 burn damage
- 🧲 Magnetic Field: Every 3 turns, special attacks are disabled for this turn

## Survival perks (for Survival mode)

- 🔋 Adrenaline Rush: Regenerates +5 energy per turn
- 💪 Iron Will: -5 damage from all attacks
- 🦇 Vampiric Aura: +5 health when doing basic attack
- 👻 Phantom Reflex: 20% chance to dodge basic attack
- 🔮 Arcane Mastery: special attack requires -10 less energy

## In-Game Actions:
- Basic Attack: deals standard damage
- Special Attack: stronger, but uses energy. Every monster type has a unique SA
- Switch Monster: switches to another alive monster from the team
- Use Potion: heals HP or restores energy
- Regenerate: regain energy slowly (limited)

## Screenshots

<img width="1914" height="1053" alt="image" src="https://github.com/user-attachments/assets/b83c5a2f-dc44-4783-9611-aa3b0d263ae1" />
<img width="1919" height="980" alt="image" src="https://github.com/user-attachments/assets/fe871773-4beb-47b6-89f6-e2eadf802cbd" />
<img width="1918" height="982" alt="image" src="https://github.com/user-attachments/assets/bc678fb6-9e22-4bac-b121-860670aa0f37" />
<img width="1915" height="978" alt="image" src="https://github.com/user-attachments/assets/ed529589-32db-4b6e-b44a-3e8c79213339" />
<img width="1916" height="983" alt="image" src="https://github.com/user-attachments/assets/054a4822-6c97-47e3-a406-e6c85fe1cb22" />
<img width="1918" height="976" alt="Снимок экрана 2025-06-14 175701" src="https://github.com/user-attachments/assets/fbe666fb-893e-48d9-bac5-7f9e25c4204b" />
<img width="1919" height="975" alt="Снимок экрана 2025-09-17 181044" src="https://github.com/user-attachments/assets/8aa4ee1b-5b29-43d8-905a-c794068ac1b9" />

## Future improvements

This project started as an academic assignment, so I was intentionally limited to vanilla Java + Java Swing and had to focus on demonstrating all the core OOP concepts. That’s why the game looks and works the way it does today. Now that the academic part is done, I’m free to experiment and explore new ideas. If I get back to this project anytime soon, these are the things that I'm planning to work on:
- Add more monster types and weapons
- Improve the battle logic and make it more advanced + smarter AI enemy behavior 
- Add graphical animations and more sound effects

Pull requests are welcome! If you’d like to add features, improve UI, or optimize code, feel free to fork and submit. 
