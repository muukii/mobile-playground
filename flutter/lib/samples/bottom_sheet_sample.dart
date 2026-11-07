import 'package:flutter/material.dart';

import 'sample_layout.dart';

class BottomSheetSample extends StatefulWidget {
  const BottomSheetSample({super.key});

  @override
  State<BottomSheetSample> createState() => _BottomSheetSampleState();
}

class _BottomSheetSampleState extends State<BottomSheetSample> {
  bool _draggable = false;

  void _openSheet() {
    showModalBottomSheet<void>(
      context: context,
      showDragHandle: true,
      isScrollControlled: _draggable,
      useSafeArea: true,
      builder: (context) {
        if (_draggable) {
          return DraggableScrollableSheet(
            expand: false,
            initialChildSize: 0.5,
            minChildSize: 0.25,
            maxChildSize: 0.9,
            builder: (context, controller) =>
                _SheetContent(controller: controller),
          );
        }
        return const _SheetContent();
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return SampleLayout(
      children: [
        const Text(
          'Compare a standard modal bottom sheet with a draggable sheet.',
        ),
        const SizedBox(height: 16),
        SwitchListTile(
          contentPadding: EdgeInsets.zero,
          title: const Text('Draggable sheet'),
          subtitle: const Text('Drag to resize, then scroll the contents.'),
          value: _draggable,
          onChanged: (value) => setState(() => _draggable = value),
        ),
        const SizedBox(height: 16),
        FilledButton(onPressed: _openSheet, child: const Text('Open sheet')),
      ],
    );
  }
}

class _SheetContent extends StatelessWidget {
  const _SheetContent({this.controller});

  final ScrollController? controller;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      child: ListView(
        controller: controller,
        shrinkWrap: controller == null,
        padding: const EdgeInsets.fromLTRB(24, 0, 24, 24),
        children: [
          Text(
            'Modal bottom sheet',
            style: Theme.of(context).textTheme.titleLarge,
          ),
          const SizedBox(height: 12),
          const Text(
            'Dismiss with the close button, a downward drag, or the scrim.',
          ),
          const SizedBox(height: 16),
          FilledButton.tonal(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Close sheet'),
          ),
          if (controller != null)
            for (var index = 1; index <= 20; index++)
              ListTile(title: Text('Item $index')),
        ],
      ),
    );
  }
}
