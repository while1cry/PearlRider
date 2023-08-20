# PearlRider 插件

PearlRider 是一个Bukkit插件，允许玩家在丢出末影珍珠后骑在上面，创造有趣的游戏体验。

## 功能

- 当玩家丢出末影珍珠后，可以骑在珍珠上方移动。
- 插件提供了配置选项，可定制是否默认启用、是否防止卡住等功能。

## 使用方法

1. 将 `PearlRider.jar` 文件放置在你的Bukkit服务器的插件文件夹中。
2. 重新启动服务器，插件将自动加载。

## 配置选项

插件提供了一些配置选项，可以在 `config.yml` 文件中进行调整：

- `enabled-by-default`：玩家是否默认启用骑行模式。
- `anti-trapped`：是否启用防止卡住的功能。
- `consumable`：末影珍珠是否可消耗。
- `world-list`：允许使用插件的世界列表。

## 命令

- `/pearlrider`：切换当前玩家的骑行模式状态。
- `/pearlrider reload`：重新加载插件配置。

## 权限
- `pearlrider.use`：拥有这个权限的玩家可以骑行在珍珠上
- `pearlrider.reload`：拥有这个权限的玩家可以重载插件

## 留言
- 这个项目由 [DJqingyi](https://www.mcbbs.net/home.php?mod=space&uid=5494509) 编写
- 我只负责代传开源

## 授权许可

PearlRider 插件使用 [GNU通用公共许可证 (GPLv3)](https://www.gnu.org/licenses/gpl-3.0.en.html) 许可。
