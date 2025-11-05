# CommandLibrary

CommandLibrary is a platform-agnostic command framework with small adapters for different runtimes. It provides a clean annotation-based API, type-safe argument mapping via transformers, topic-based help, tab completion, permission and sender checks, and pluggable async execution.

Modules
- commandlibrary-core: platform-neutral core (annotations, parser, manager, transformers API)
- platform-bukkit: Bukkit adapter library
- platform-cli: CLI adapter library
- example-bukkit: a minimal Bukkit plugin using the facade and sample commands
- example-cli: a minimal CLI app using the CLI adapter

Quick Start
- Bukkit (Kotlin)
  - Add dependency on `platform-bukkit` (and `spigot-api`). In your JavaPlugin:
  ```kotlin
  class MyPlugin : JavaPlugin() {
      override fun onEnable() {
          BukkitCommands.install(this, MyCommands::class.java, MyCommandB::class.java)
      }
  }
  ```
  - Define commands (Kotlin/Java both supported):
  ```kotlin
  import cn.monshine.commandlibrary.annotation.Command
  import cn.monshine.commandlibrary.annotation.Param
  import org.bukkit.entity.Player

  object MyCommands {
      @JvmStatic
      @Command(names = ["hello"], description = "Say hi")
      fun hello(sender: Player, @Param(name = "who", defaultValue = "world") who: String) {
          sender.sendMessage("&aHello, $who!")
      }
  }
  ```
  - PlayerTransformer is registered automatically by the Bukkit adapter; basic type transformers (String/Boolean/Int/Float/Double) are auto-registered by default.

- CLI (Kotlin)
  ```kotlin
  import cn.monshine.commandlibrary.CommandManager
  import cn.monshine.commandlibrary.cli.CLIEnvironment
  import cn.monshine.commandlibrary.cli.CLIRegistrar
  import cn.monshine.commandlibrary.cli.CliSender

  val env = CLIEnvironment(ansi = true) // set ansi=false to strip &-codes
  val registrar = CLIRegistrar()
  val manager = CommandManager(env, registrar)
  manager.registerCommands(CliCommands::class.java)

  val sender = CliSender(name = "cli", roles = setOf("op"))
  registrar.executeLine(sender, "/echo hello world")
  ```
  See example-cli for a tiny REPL app.

Annotations and Parameters
- @Command(names, description, permission, playerOnly, async)
  - `names`: support subcommands via spaces, e.g. `"root sub a"`
  - `permission`: empty allows all; value `op` checks operator/op-role
  - `playerOnly`: requires platform sender to be a player (Bukkit)/true (CLI default)
  - `async`: run in the environment’s async executor
- @Param(name, defaultValue, wildcard, type)
  - `defaultValue`: used if the argument is omitted
  - `wildcard=true`: capture the rest of the line as one String
  - `type=FLAG`: boolean flag recognized by `-name`
- @CommandTopicGetter(label)
  - Provide a CommandTopic for a label path; help output groups by topics

Transformers
- Built-in (auto-registered): String, Boolean, Int, Float, Double
- Bukkit adapter auto-registers: org.bukkit.entity.Player
- Register more via `manager.registerTransformer(MyType::class.java, MyTransformer())`

Environment and Registrar
- `CommandEnvironment<S>`: messaging, permission, player/op checks, formatting, async executor
- `CommandRegistrar<S>`: register root labels with the host platform; also provides `initialize(manager)` to install default and platform-specific transformers
- Bukkit: uses server scheduler for async; translates `&` color codes using ChatColor
- CLI: can colorize `&` codes to ANSI (configurable with `ansi` flag) or strip them; inject your own `ExecutorService` for async

Color Formatting
- Use `&` codes in messages (e.g., `&cError`, `&7details`, styles: `&l`, `&n`, `&o`, `&m`, reset: `&r`)
- Bukkit: uses ChatColor.translateAlternateColorCodes
- CLI: `CLIEnvironment(ansi=true)` renders ANSI sequences; `ansi=false` strips color codes to plain text

Async Execution
- `@Command(async = true)` delegates to `env.runAsync { ... }`
- Bukkit: backed by the server scheduler
- CLI: pass your own `ExecutorService` when constructing `CLIEnvironment`

Tab Completion
- Subcommand labels are suggested by the manager
- Parameter completion is provided by each ParameterTransformer

Examples
- Bukkit plugin example: `example-bukkit`
- CLI app example: `example-cli`

Build and Test
- Build all modules:
```
mvn clean package
```

Extending To New Platforms
- Implement `CommandEnvironment<S>` and `CommandRegistrar<S>`
- Construct `CommandManager<S>(env, registrar)`; registrar.initialize(manager) will install basic transformers
- Provide platform transformers and a minimal facade (like BukkitCommands) to simplify usage

License
- MIT (see LICENSE)
