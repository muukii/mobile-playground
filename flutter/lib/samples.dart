import 'package:flutter/material.dart';

import 'samples/alignment_sample.dart';
import 'samples/animation_sample.dart';
import 'samples/bottom_sheet_sample.dart';

class PlaygroundSample {
  const PlaygroundSample({
    required this.id,
    required this.title,
    required this.category,
    required this.description,
    required this.builder,
  });

  final String id;
  final String title;
  final String category;
  final String description;
  final WidgetBuilder builder;
}

final playgroundSamples = <PlaygroundSample>[
  PlaygroundSample(
    id: 'alignment',
    title: 'Alignment',
    category: 'Layout',
    description: 'Move a child through the nine positions of an Align widget.',
    builder: (context) => const AlignmentSample(),
  ),
  PlaygroundSample(
    id: 'animation',
    title: 'Animation',
    category: 'Motion',
    description: 'Animate size, color, and corner radius with state changes.',
    builder: (context) => const AnimationSample(),
  ),
  PlaygroundSample(
    id: 'bottom-sheet',
    title: 'Bottom sheet',
    category: 'Presentation',
    description:
        'Present a modal sheet and try a draggable, scrollable variant.',
    builder: (context) => const BottomSheetSample(),
  ),
];
