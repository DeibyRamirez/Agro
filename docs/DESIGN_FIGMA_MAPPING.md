# Mapeo AgroGest (Figma) ↔ AgroControl (Android)

**Figma:** [AgroGest](https://www.figma.com/design/WeMBtK1xWP0qNTtwllbwcL/AgroGest?node-id=0-1)

Este documento enlaza el diseño de referencia con los layouts y pantallas del repositorio. Si exportas frames desde Figma, añade capturas en `docs/figma/` y actualiza la columna **Frame Figma**.

## Design tokens (implementados en código)

| Token | Valor | Uso en app |
|-------|-------|------------|
| `primary` | `#21852A` | Header, botones, enlaces activos |
| `textPrimary` | `#26352A` | Títulos de sección |
| `textBody` | `#444444` | Detalle en tarjetas |
| `textMuted` | `#666666` | Subtítulos |
| `surfaceList` | `#F5F7F5` | Fondo listado animales |
| `surfaceCard` | `#FFFFFF` | Tarjetas |
| `chipSexoBg` | `#E7F3E7` | Badge sexo |
| `kpiAnimales` | `#91DF87` / `#174B20` | Tarjeta dashboard animales |
| `kpiProduccion` | `#A8D9FF` | Tarjeta producción |
| `kpiPotreros` | `#E7FF80` / `#35512C` | Tarjeta potreros |
| `kpiReproduccion` | `#F1B6B9` / `#542020` | Tarjeta reproducción |
| `kpiReportes` | `#C9A8E5` / `#342144` | CTA reportes |
| `inputBg` | `#F4F0F0` | Campos de formulario |

Centralizar en [`app/src/main/res/values/colors.xml`](../app/src/main/res/values/colors.xml) es recomendable en iteraciones futuras.

## Pantallas

| Frame Figma (nombre sugerido) | Layout Android | Activity | Estado |
|------------------------------|----------------|----------|--------|
| Inicio / Dashboard | `activity_main.xml` | `MainActivity` | KPI animales dinámico; resto demo + Toast |
| Animales — lista | `activity_animales.xml` | `AnimalesActivity` | Lista Room, búsqueda, detalle |
| Animales — alta | `activity_agregar_animal.xml` | `AgregarAnimalActivity` | Formulario + Room |
| Animales — detalle | `activity_detalle_animal.xml` | `DetalleAnimalActivity` | Ficha; edición/módulos próximamente |
| Producción | — | — | Pendiente |
| Potreros | — | — | Pendiente |
| Reproducción | — | — | Pendiente |
| Finanzas | — | — | Pendiente |
| Reportes | — | — | Pendiente |
| Más | — | — | Pendiente |

## Componentes

| Componente Figma | Recurso Android |
|------------------|-----------------|
| Tarjeta animal | `item_animal_card.xml` |
| Barra inferior 5 ítems | Sección inferior en `activity_main.xml` |
| Campo búsqueda | `etBuscarAnimal` en `activity_animales.xml` |

## Flujos

```text
MainActivity → (tarjeta/nav Animales) → AnimalesActivity
AnimalesActivity → AgregarAnimalActivity → guarda en Room → vuelve a lista
AnimalesActivity → tap tarjeta → DetalleAnimalActivity
```

## Cómo completar el mapeo con Figma

1. En Figma, exportar lista de páginas/frames (Dev Mode o plugin "Design Doc").
2. Anotar tipografía (familia, pesos, tamaños) y radios si difieren de los XML.
3. Añadir filas a la tabla de pantallas con el nombre exacto del frame.
4. Opcional: plugin Figma-to-Android solo para referencia; este proyecto usa XML manual + View Binding.
