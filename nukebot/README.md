# NukeBot

***Ядерный Бот***

## Packages

### evaluationfunc

Пакет с функциями оценки состояния доски:

- [x] Оценка стоимости фигур в пешках
- [x] Оценка позиции фигур
- [x] Оценка [PeSTO](https://www.chessprogramming.org/PeSTO%27s_Evaluation_Function)

### searchfunc

Пакет с разными реализациями поиска лучшего значения.

- [x] Линейный поиск
- [x] Параллельный поиск на основе ExecutorService
- [ ] Параллельный поиск на основе ForkJoinPool

### searchalg

Пакет с конкретными реализациями алгоритмов поиска. Включает в себя следующие алгоритмы:

- ***MinimaxAlfaBetaPruning***

Стандартный [минимакс алгоритм](https://en.wikipedia.org/wiki/Minimax)
с [альфа-бета отсечениями](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning).

- ***NegamaxAlfaBetaPruning***

Стандартный [негамакс алгоритм](https://en.wikipedia.org/wiki/Negamax) с альфа-бета отсечениями.

- ***NegaScoutAlfaBetaPruning***

Улучшенный [негамакс алгоритм (негаскаут)](https://www.chessprogramming.org/NegaScout) с нулевым
окном.

- ***MinimaxWithTT***

Улучшенный [минимакс с таблицами транспонирования](https://people.csail.mit.edu/plaat/mtdf.html).

- ***NegaScoutWithTT***

Улучшенный негаскаут с таблицами транспонирования.

- ***PVSNullMoveWithTT***

Алгоритм [PVS](https://www.chessprogramming.org/Principal_Variation_Search) - Principal Variation
Search, реализующий [Null-Move Heuristic](https://www.chessprogramming.org/Null_Move_Pruning).

- ***PVSVerifiedNullMoveWithTT***

Алгоритм [PVS](https://www.chessprogramming.org/Principal_Variation_Search) с проверенным нулевым
ходом ([Verified Null-Move](https://arxiv.org/abs/0808.1125)).

- ***UltimateQuintessence***

*Высшая Квинтэссенция* - лично мой алгоритм, основанный на PVS. Собран из различных алгоритмов и
эвристик. Включает в себя улучшения, описанные ниже.

## Roadmap

- [x] [Альфа-бета отсечения](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning)
- [x] Таблицы транспонирования (ТТ) + совместимость с [MTDF](https://en.wikipedia.org/wiki/MTD(f))
- [x] [Verified Null-Move](https://arxiv.org/abs/0808.1125)
- [x] Функция оценивания [quiesce](https://www.chessprogramming.org/Quiescence_Search) + ТТ
- [x] Сортировка ходов [MVV-LVA](https://www.chessprogramming.org/MVV-LVA) - улучшение quiesce
- [ ] [Delta Pruning](https://www.chessprogramming.org/Delta_Pruning) - улучшение quiesce
- [ ] [SEE](https://www.chessprogramming.org/Static_Exchange_Evaluation) - улучшение quiesce
- [ ] [Guard Heuristic](https://www.chessprogramming.org/Guard_Heuristic) - улучшение quiesce
- [ ] [History Heuristic](https://www.chessprogramming.org/History_Heuristic) - улучшение quiesce
- [ ] [Relative History Heuristic](https://www.chessprogramming.org/Relative_History_Heuristic)
- [ ] [LMR](https://www.chessprogramming.org/Late_Move_Reductions)
- [ ] Iterative deepening
- [ ] [Parity Pruning](https://www.chessprogramming.org/Parity_Pruning)
- [ ] Aspiration Search
- [ ] [Aspiration Windows](https://www.chessprogramming.org/Aspiration_Windows)
- [ ] Улучшения для снижения Horizon effect
- [ ] [ProbCut](https://www.chessprogramming.org/ProbCut)
- [ ] [Multi-Cut](https://www.chessprogramming.org/Multi-Cut)
- [ ] [RankCut](https://www.chessprogramming.org/RankCut)
- [ ] [SEX](https://www.chessprogramming.org/SEX_Algorithm) - реализует "интересность" хода

## Ссылки

1. [Сайт по программированию шахмат](https://www.chessprogramming.org)
2. [Статья по алгоритмам и эвристикам](https://homepages.cwi.nl/~paulk/theses/Carolus.pdf)
3. [Википедия](https://en.wikipedia.org/wiki/Minimax)
