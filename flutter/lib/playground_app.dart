import 'package:flutter/material.dart';

import 'samples.dart';

class PlaygroundApp extends StatelessWidget {
  const PlaygroundApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Playground',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(colorSchemeSeed: Colors.indigo),
      darkTheme: ThemeData(
        colorSchemeSeed: Colors.indigo,
        brightness: Brightness.dark,
      ),
      home: const _Catalog(),
    );
  }
}

class _Catalog extends StatelessWidget {
  const _Catalog();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Flutter Playground')),
      body: SafeArea(
        child: Center(
          child: ConstrainedBox(
            constraints: const BoxConstraints(maxWidth: 640),
            child: ListView.separated(
              padding: const EdgeInsets.all(16),
              itemCount: playgroundSamples.length,
              separatorBuilder: (context, index) => const SizedBox(height: 12),
              itemBuilder: (context, index) {
                final sample = playgroundSamples[index];
                return Card.outlined(
                  clipBehavior: Clip.antiAlias,
                  child: ListTile(
                    key: ValueKey('sample-${sample.id}'),
                    contentPadding: const EdgeInsets.all(16),
                    title: Text(sample.title),
                    subtitle: Text('${sample.category}\n${sample.description}'),
                    isThreeLine: true,
                    trailing: const Icon(Icons.chevron_right),
                    onTap: () => Navigator.of(context).push<void>(
                      MaterialPageRoute(
                        settings: RouteSettings(name: '/samples/${sample.id}'),
                        builder: (context) => Scaffold(
                          key: ValueKey('sample-${sample.id}-screen'),
                          appBar: AppBar(title: Text(sample.title)),
                          body: SafeArea(child: sample.builder(context)),
                        ),
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
        ),
      ),
    );
  }
}
