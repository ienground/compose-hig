# 성능 점검 이슈

반복 비용이 발생하는 코드 경로와 실제 성능 저하의 입증을 구분한다. 아래 항목은 계측 후 수정 범위를 결정한다.

| ID | 우선순위 | 이슈 이름 | 위치 | 해결 방향 | 상태 |
| --- | --- | --- | --- | --- | --- |
| PERF-001 | P2 | 자동 대비 분석이 정지 화면에서도 전체 레이어 비트맵을 반복 생성 | [hig/src/commonMain/kotlin/zone/ien/hig/CupertinoLiquidButton.kt:163](../../hig/src/commonMain/kotlin/zone/ien/hig/CupertinoLiquidButton.kt#L163)<br>[hig/src/commonMain/kotlin/zone/ien/hig/CupertinoTopBars.kt:523](../../hig/src/commonMain/kotlin/zone/ien/hig/CupertinoTopBars.kt#L523)<br>[hig/src/commonMain/kotlin/zone/ien/hig/Luminance.kt:36](../../hig/src/commonMain/kotlin/zone/ien/hig/Luminance.kt#L36) | isBackgroundAdaptive=true이면 루프마다 toImageBitmap으로 전체 레이어를 읽고 crop·scale·픽셀 버퍼를 만든 뒤 300ms 대기한다. 복수 버튼에서 독립적으로 반복되는 경로는 확인했으며 프레임·메모리 영향은 미측정이다. 대표 화면에서 CPU/GPU readback·할당을 계측하고, 그리기 변경 시 재계산하거나 작은 해상도로 렌더링한 뒤 읽는 경로·공유가 가능한지 검토한다. 전체 레이어를 읽은 뒤 scale만 적용하는 방식은 개선으로 간주하지 않는다. 대비 전환 동작을 보존하며 계측 전후를 비교한다. | 검증 필요 |
