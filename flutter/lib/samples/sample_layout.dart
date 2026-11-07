import 'package:flutter/material.dart';

class SampleLayout extends StatelessWidget {
  const SampleLayout({required this.children, super.key});

  final List<Widget> children;

  @override
  Widget build(BuildContext context) {
    return Center(
      child: ConstrainedBox(
        constraints: const BoxConstraints(maxWidth: 640),
        child: ListView(padding: const EdgeInsets.all(24), children: children),
      ),
    );
  }
}
