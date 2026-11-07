import 'package:flutter/material.dart';

import 'sample_layout.dart';

class AlignmentSample extends StatefulWidget {
  const AlignmentSample({super.key});

  @override
  State<AlignmentSample> createState() => _AlignmentSampleState();
}

class _AlignmentSampleState extends State<AlignmentSample> {
  double _horizontal = 0;
  double _vertical = 0;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    return SampleLayout(
      children: [
        const Text(
          'Change either axis to explore how Align positions its child.',
        ),
        const SizedBox(height: 24),
        Container(
          height: 240,
          padding: const EdgeInsets.all(12),
          decoration: BoxDecoration(
            color: colors.surfaceContainerHighest,
            borderRadius: BorderRadius.circular(16),
          ),
          child: Align(
            key: const ValueKey('alignment-preview'),
            alignment: Alignment(_horizontal, _vertical),
            child: Container(
              width: 56,
              height: 56,
              decoration: BoxDecoration(
                color: colors.primary,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Icon(Icons.flutter_dash, color: colors.onPrimary),
            ),
          ),
        ),
        const SizedBox(height: 24),
        const Text('Horizontal'),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8,
          children: [
            for (final (value, label) in [
              (-1.0, 'Left'),
              (0.0, 'Center'),
              (1.0, 'Right'),
            ])
              ChoiceChip(
                label: Text(label),
                selected: _horizontal == value,
                onSelected: (_) => setState(() => _horizontal = value),
              ),
          ],
        ),
        const SizedBox(height: 16),
        const Text('Vertical'),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8,
          children: [
            for (final (value, label) in [
              (-1.0, 'Top'),
              (0.0, 'Middle'),
              (1.0, 'Bottom'),
            ])
              ChoiceChip(
                label: Text(label),
                selected: _vertical == value,
                onSelected: (_) => setState(() => _vertical = value),
              ),
          ],
        ),
      ],
    );
  }
}
