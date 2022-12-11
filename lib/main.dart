import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]).then((value) => runApp(const MyApp()));
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    const title = "Androntainer";
    return const CupertinoApp(
      title: title,
      theme: CupertinoThemeData(brightness: Brightness.light),
      home: MyHomePage(title: title),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const channel = MethodChannel("android");
  final String origin = "origin";
  int _counter = 1145141919810;

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
    //origin();
  }

  void _origin() {
    try {
      Future future = channel.invokeMethod(origin);
      if (kDebugMode) {
        print(future.toString());
      }
    } on PlatformException catch (e) {
      if (kDebugMode) {
        print(e.toString());
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return CupertinoTabScaffold(
      // appBar: AppBar(
      //   title: Text(widget.title),
      // ),
      // body: Center(
      //   child: Column(
      //     mainAxisAlignment: MainAxisAlignment.center,
      //     children: <Widget>[
      //       const Text(
      //         '压力马斯内',
      //       ),
      //       const Text(
      //         'You have pushed the button this many times:',
      //       ),
      //       Text(
      //         '$_counter',
      //         style: Theme.of(context).textTheme.headline4,
      //       ),
      //       TextButton(
      //           onPressed: _incrementCounter,
      //           child: const Text("增加")
      //       ),
      //       TextButton(
      //           onPressed: _origin,
      //           child: const Text("隐藏")
      //       )
      //     ],
      //   ),
      // ),

      // navigationBar: CupertinoNavigationBar(
      //   middle: Text('Chat App'),
      // ),

      tabBar: CupertinoTabBar(
        currentIndex: 1,
        items: const <BottomNavigationBarItem>[
          // 3 <-- SEE HERE
          BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.phone), label: 'Calls'),
          BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.chat_bubble_2), label: 'Chats'),
          BottomNavigationBarItem(
              icon: Icon(CupertinoIcons.settings), label: 'Settings'),
        ],
      ),
      tabBuilder: (context, index) {
        late final CupertinoTabView returnValue;
        switch (index) {
          case 0:
            // 4 <-- SEE HERE
            returnValue = CupertinoTabView(
              builder: (context) {
                return const CupertinoPageScaffold(
                    navigationBar: CupertinoNavigationBar(
                      middle: Text('Calls'),
                    ),
                    child: Center(child: Text('Calls')));
              }
            );
            break;
          case 1:
            returnValue = CupertinoTabView(
              builder: (context) {
                return const CupertinoPageScaffold(
                    navigationBar: CupertinoNavigationBar(
                      middle: Text('Chats'),
                    ),
                    child: Center(child: Text('Chats')));
              }
            );
            break;
          case 2:
            returnValue = CupertinoTabView(
              builder: (context) {
                return const CupertinoPageScaffold(
                    navigationBar: CupertinoNavigationBar(
                      middle: Text('Settings'),
                    ),
                    child: Center(child: Text('Settings')));
              },
            );
            break;
        }
        return returnValue;
      },

      // children: [
      //   Center(
      //     child: Text('Hi'),
      //   ),
      // ],
    );
  }
}
