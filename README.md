JavaIndexer
===========

Для сборки необходимы maven и java 7 (протестированно на Ubuntu 14.04 и MacOsX Mavericks).
Чтобы скомпилировать проект, перейдите в папку с проектом и выполните 'mvn compile'.
Для запуска тестов используйте 'mvn test'. Один из тестов потребует довольно много времени (около 40 секунд). Он тестирует асинхронные события.

Чтобы запустить пример, демонстрирующий работу библиотеки, выполните 'mvn exec:java'. Программа предложит вам выполнить нужные команды для работы с диском, а именно:  

Commands:  

a [file_or_dir_path] - add file or dir to index

r [file_or_dir_path] - remove file or dir from index

s [word]             - get list of files containing [word]

c [file_path]        - check if index contains file

p                    - show previous commands results 

h                    - show this help

q                    - finish work

Все команды выполняются в отдельных потоках и их результаты накапливаются в очередь. Когда repl принимает очередную команду, она вывод список результатов предыдущих комманд. Так же, для того, чтобы посмотреть накопленные результаты, есть отдельная команда 'p'

Как написать свой индексатор, используя библиотеку
==================================================

Индексатор состоит из 4ёх частей:

1. Индекс (задаётся интерфейсом indexer.index.FileIndex). Он хранит содержимое файлов. При создании индекса ему на вход подаётся лексер (задаётся интерфейсом indexer.tokenizer.Tokenizer), с помощью которого он делит файлы на токены.

2. Менеджер мониторов директорий. Отслеживаниет события добавления, удаления и модификации в папках файловой системы. Для каждой папки, представляющей корень отдельного дерева, создаётся отдельный монитор.

3. Обработчик событий индекса (задаётся интерфейсом indexer.handler.IndexEventsHandler). Содержит callback'и для событий добавления, удаления и модификации. Обепечивает взаимодейсвие Индекса с внешним миром: когда пользователь делает какой либо запрос к индексу или монитор получет событие об изменениях в папках, callback'и IndexEventsHandler'а вызываются для выполнения соответствующих изменений в индексе или запросов к нему.

4. Обработчик жизненного цикла мониторов (задаётся интерфейсом indexer.fsmonitor.FSMonitorLifecycleHandler). Содержит callback'и для событий перезапуска и выключения монитора. Используется Менеджером мониторов директорий для реакции на указанные события (менеджер перезапускает монитор, если в процессе его работы возникает ошибка; если после определённого числа перезапусков монитор снова ломается, то менеджер отключает его совсем и отслеживание изменений с соответствующей папке прекращается).

Для написания собственного индексатора нужно написать классы, реализующие указанные выше интерфейсы и создать экземпляр класса FSIndexer. Его конструктор принимает объекты этих классов.
