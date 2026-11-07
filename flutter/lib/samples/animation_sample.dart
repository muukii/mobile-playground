import 'package:flutter/material.dart';

import 'sample_layout.dart';

class AnimationSample extends StatefulWidget {
  const AnimationSample({super.key});

  @override
  State<AnimationSample> createState() => _AnimationSampleState();
}

class _AnimationSampleState extends State<AnimationSample> {
  bool _expanded = false;
  double _duration = 600;

  @override
  Widget build(BuildContext context) {
    final colors = Theme.of(context).colorScheme;
    return SampleLayout(
      children: [
        const Text(
          'Toggle again during the animation to change its destination.',
        ),
        SizedBox(
          height: 260,
          child: Center(
            child: AnimatedContainer(
              key: const ValueKey('animation-preview'),
              duration: Duration(milliseconds: _duration.round()),
              curve: Curves.easeInOutCubic,
              width: _expanded ? 200 : 80,
              height: _expanded ? 200 : 80,
              decoration: BoxDecoration(
                color: _expanded ? colors.tertiary : colors.primary,
                borderRadius: BorderRadius.circular(_expanded ? 48 : 12),
              ),
            ),
          ),
        ),
        FilledButton.tonal(
          onPressed: () => setState(() => _expanded = !_expanded),
          child: Text(_expanded ? 'Shrink' : 'Expand'),
        ),
        const SizedBox(height: 24),
        Text('Duration: ${_duration.round()} ms'),
        Slider(
          value: _duration,
          min: 100,
          max: 1500,
          divisions: 14,
          label: '${_duration.round()} ms',
          semanticFormatterCallback: (value) => '${value.round()} milliseconds',
          onChanged: (value) => setState(() => _duration = value),
        ),
      ],
    );
  }
}
