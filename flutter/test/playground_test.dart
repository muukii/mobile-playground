import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_playground/playground_app.dart';
import 'package:flutter_playground/samples.dart';

Future<void> openSample(WidgetTester tester, String id) async {
  final entry = find.byKey(ValueKey('sample-$id'));
  await tester.scrollUntilVisible(entry, 200);
  await tester.ensureVisible(entry);
  await tester.pumpAndSettle();
  await tester.tap(entry);
  await tester.pumpAndSettle();
}

void main() {
  WidgetController.hitTestWarningShouldBeFatal = true;
  test('sample IDs are unique and stable route components', () {
    final ids = playgroundSamples.map((sample) => sample.id).toList();
    expect(ids.toSet().length, ids.length);
    expect(ids, everyElement(matches(RegExp(r'^[a-z0-9-]+$'))));
  });

  for (final sample in playgroundSamples) {
    testWidgets('${sample.title} opens and returns to the catalog', (
      tester,
    ) async {
      await tester.pumpWidget(const PlaygroundApp());
      await openSample(tester, sample.id);

      expect(
        find.byKey(ValueKey('sample-${sample.id}-screen')),
        findsOneWidget,
      );
      await tester.pageBack();
      await tester.pumpAndSettle();
      expect(find.text('Flutter Playground'), findsOneWidget);

      await openSample(tester, sample.id);
      await tester.binding.handlePopRoute();
      await tester.pumpAndSettle();
      expect(find.text('Flutter Playground'), findsOneWidget);
      expect(tester.takeException(), isNull);
    });
  }

  testWidgets('alignment controls reach all nine positions', (tester) async {
    await tester.pumpWidget(const PlaygroundApp());
    await openSample(tester, 'alignment');
    for (final (x, horizontal) in [
      (-1.0, 'Left'),
      (0.0, 'Center'),
      (1.0, 'Right'),
    ]) {
      await tester.tap(find.widgetWithText(ChoiceChip, horizontal));
      for (final (y, vertical) in [
        (-1.0, 'Top'),
        (0.0, 'Middle'),
        (1.0, 'Bottom'),
      ]) {
        await tester.tap(find.widgetWithText(ChoiceChip, vertical));
        await tester.pumpAndSettle();
        final preview = tester.widget<Align>(
          find.byKey(const ValueKey('alignment-preview')),
        );
        expect(preview.alignment, Alignment(x, y));
      }
    }
  });

  testWidgets('animation can reverse in flight and change duration', (
    tester,
  ) async {
    await tester.pumpWidget(const PlaygroundApp());
    await openSample(tester, 'animation');
    final preview = find.byKey(const ValueKey('animation-preview'));
    expect(tester.getSize(preview), const Size(80, 80));

    await tester.tap(find.text('Expand'));
    await tester.pump();
    await tester.pump(const Duration(milliseconds: 300));
    expect(tester.getSize(preview).width, inExclusiveRange(80, 200));
    await tester.tap(find.text('Shrink'));
    await tester.pumpAndSettle();
    expect(tester.getSize(preview), const Size(80, 80));

    await tester.tap(find.text('Expand'));
    await tester.pumpAndSettle();
    expect(tester.getSize(preview), const Size(200, 200));

    final slider = find.byType(Slider);
    await tester.tapAt(tester.getTopRight(slider) + const Offset(-24, 16));
    await tester.pumpAndSettle();
    expect(
      tester.widget<AnimatedContainer>(preview).duration,
      const Duration(milliseconds: 1500),
    );
    expect(find.text('Duration: 1500 ms'), findsOneWidget);
  });

  testWidgets('modal sheet closes by button, scrim, drag, and system Back', (
    tester,
  ) async {
    await tester.pumpWidget(const PlaygroundApp());
    await openSample(tester, 'bottom-sheet');
    for (final dismissal in ['button', 'scrim', 'drag', 'back']) {
      await tester.tap(find.text('Open sheet'));
      await tester.pumpAndSettle();
      expect(find.text('Modal bottom sheet'), findsOneWidget);

      switch (dismissal) {
        case 'button':
          await tester.tap(find.text('Close sheet'));
        case 'scrim':
          await tester.tapAt(const Offset(16, 100));
        case 'drag':
          final sheet = tester.getRect(find.byType(BottomSheet));
          await tester.flingFrom(
            sheet.topCenter + const Offset(0, 16),
            const Offset(0, 400),
            1000,
          );
        case 'back':
          await tester.binding.handlePopRoute();
      }
      await tester.pumpAndSettle();
      expect(find.text('Modal bottom sheet'), findsNothing, reason: dismissal);
      expect(find.text('Open sheet'), findsOneWidget);
    }
  });

  testWidgets('draggable sheet expands and scrolls to its last item', (
    tester,
  ) async {
    await tester.pumpWidget(const PlaygroundApp());
    await openSample(tester, 'bottom-sheet');
    await tester.tap(find.byType(SwitchListTile));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Open sheet'));
    await tester.pumpAndSettle();
    expect(find.byType(DraggableScrollableSheet), findsOneWidget);

    final sheet = find.byType(BottomSheet);
    final initialHeight = tester.getSize(sheet).height;
    await tester.drag(find.text('Modal bottom sheet'), const Offset(0, -200));
    await tester.pumpAndSettle();
    expect(tester.getSize(sheet).height, greaterThan(initialHeight));

    final content = find.descendant(
      of: sheet,
      matching: find.byType(Scrollable),
    );
    await tester.scrollUntilVisible(
      find.text('Item 20'),
      200,
      scrollable: content,
    );
    await tester.pumpAndSettle();
    expect(find.text('Item 20').hitTestable(), findsOneWidget);
    await tester.binding.handlePopRoute();
    await tester.pumpAndSettle();
    expect(find.text('Open sheet'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('leaving an experiment resets its local state', (tester) async {
    await tester.pumpWidget(const PlaygroundApp());
    await openSample(tester, 'animation');
    await tester.tap(find.text('Expand'));
    await tester.pumpAndSettle();
    await tester.pageBack();
    await tester.pumpAndSettle();
    await openSample(tester, 'animation');
    expect(find.text('Expand'), findsOneWidget);
    expect(
      tester.getSize(find.byKey(const ValueKey('animation-preview'))),
      const Size(80, 80),
    );
  });

  for (final sample in playgroundSamples) {
    testWidgets(
      '${sample.title} supports a narrow screen, large text, and dark mode',
      (tester) async {
        tester.view.physicalSize = const Size(320, 568);
        tester.view.devicePixelRatio = 1;
        tester.platformDispatcher.textScaleFactorTestValue = 2;
        tester.platformDispatcher.platformBrightnessTestValue = Brightness.dark;
        addTearDown(tester.view.resetPhysicalSize);
        addTearDown(tester.view.resetDevicePixelRatio);
        addTearDown(tester.platformDispatcher.clearTextScaleFactorTestValue);
        addTearDown(tester.platformDispatcher.clearPlatformBrightnessTestValue);

        await tester.pumpWidget(const PlaygroundApp());
        await openSample(tester, sample.id);
        expect(tester.takeException(), isNull);
        final context = tester.element(
          find.byKey(ValueKey('sample-${sample.id}-screen')),
        );
        expect(Theme.of(context).brightness, Brightness.dark);
        if (sample.id == 'bottom-sheet') {
          await tester.scrollUntilVisible(find.text('Open sheet'), 200);
          await tester.pumpAndSettle();
          await tester.tap(find.text('Open sheet'));
          await tester.pumpAndSettle();
          await tester.scrollUntilVisible(
            find.text('Close sheet'),
            100,
            scrollable: find.descendant(
              of: find.byType(BottomSheet),
              matching: find.byType(Scrollable),
            ),
          );
          await tester.pumpAndSettle();
          await tester.tap(find.text('Close sheet'));
          await tester.pumpAndSettle();
        }
        expect(tester.takeException(), isNull);
      },
    );
  }
}
