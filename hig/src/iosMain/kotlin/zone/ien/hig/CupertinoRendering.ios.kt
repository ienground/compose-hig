package zone.ien.hig

import kotlinx.cinterop.ExperimentalForeignApi
import zone.ien.hig.uikit.rendering.HIGEnableParallelRenderingFix

/**
 * Compose 1.12.2의 iOS 병렬 렌더러가 합성 중인 버퍼를 재사용하지 않도록 보정한다.
 *
 * 메인 스레드에서 첫 ComposeUIViewController를 만들기 전에 호출한다.
 * 프로세스 전체의 CMPMetalLayer 버퍼 획득 경로에 한 번 적용되며, 반복 호출해도 중복 적용하지 않는다.
 * 병렬 렌더링, opaque 및 블러 설정은 바꾸지 않는다.
 * 사용 가능한 버퍼를 우선 재사용하고, 부족할 때만 최대 5개까지 늘린다.
 *
 * Compose 내부 구현에 의존하므로 의존성 업데이트 시 호환성과 보정 필요 여부를 다시 확인해야 한다.
 *
 * @return 적용되었거나 이미 적용되어 있으면 true. 메인 스레드가 아니거나 내부 구조가 호환되지 않으면 false.
 */
@OptIn(ExperimentalForeignApi::class)
fun enableCupertinoParallelRenderingFix(): Boolean = HIGEnableParallelRenderingFix()
