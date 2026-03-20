import os

root = r'C:\Users\KSmith3\AndroidStudioProjects\MathWords'
files = [
    r'app\src\main\java\io\github\anodoze\mathwords\design.md',
    r'app\src\main\java\io\github\anodoze\mathwords\Answer.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\AnswerDao.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\AwaitingInput.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\Card.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\CardDao.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\Converters.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\DatabaseSeeder.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\HomeScreen.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\HomeViewModel.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\HomeViewModelFactory.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\MainActivity.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\MathWordsApplication.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\MathWordsDatabase.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\Navigation.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\Operation.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\QuizScreen.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\QuizState.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\QuizViewModel.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\QuizViewModelFactory.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\Scheduler.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\SettingsScreen.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\UIComponents.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\UserSettings.kt',
    r'app\src\main\java\io\github\anodoze\mathwords\WrongAnswer.kt',
]

output = os.path.join(root, 'mathwords_context.txt')
if os.path.exists(output):
    os.remove(output)

for rel in files:
    filepath = os.path.join(root, rel)
    with open(output, 'a', encoding='utf-8') as out:
        out.write(f'// === {rel} ===\n')
        try:
            with open(filepath, encoding='utf-8') as f:
                out.write(f.read())
        except FileNotFoundError:
            out.write(f'FILE NOT FOUND\n')
        out.write('\n')

print(f'Written to {output}')