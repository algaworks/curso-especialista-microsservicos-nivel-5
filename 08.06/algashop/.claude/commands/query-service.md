# Query Service with Filters

Implement a complete read-only query service with pageable, sortable filters for the entity **$ARGUMENTS**.

---

## Phase 1 — Discover the project context

Before writing any code, explore the microservice to understand its patterns:

1. **Identify the microservice**: Determine which microservice the entity belongs to, based on the current working directory or context clues in the conversation.

2. **Find an existing query service as a reference**: Search for `*QueryService*`, `*QueryApplicationService*`, `ForQuerying*` files. Study the one closest to the entity being created.

3. **Extract the following from the reference**:
   - Package structure (root package, layer naming: `core/ports`, `application`, `infrastructure`, `presentation`, etc.)
   - ID type (`Long` TSID, `UUID`, or plain `Long`)
   - How filtering is implemented: **raw Criteria API** (`EntityManager`) or **Spring Data Specifications** (`JpaSpecificationExecutor`)
   - Filter base class (does it extend `SortablePageFilter`, `PageFilter`, or is it a plain class?)
   - How the service is wired: concrete `@Service` injected directly into the controller, or implemented behind a port/interface
   - Output DTO pattern: `static from()` factory, `Mapper` interface (ModelMapper), or `builder.construct()` projection
   - How `PageModel` is structured and where it lives
   - Repository ID type and whether `JpaSpecificationExecutor` is already extended
   - Any existing exception class for entity-not-found (`{Entity}NotFoundException`)

4. **Summarize** what you found before generating any file. List: architecture style, package root, filtering strategy, mapper strategy, and reference files.

---

## Phase 2 — Ask clarifying questions

Before generating, confirm with the user:

- What **filterable fields** does the entity have? (name, type, status, dates, ranges, related entity IDs)
- What **sortable fields** should be exposed?
- What should **`{Entity}SummaryOutput`** contain? (minimal fields for the list view)
- What should **`{Entity}DetailOutput`** contain? (all fields for the single-record view)
- Are there **nested relationships** to include? (e.g., customer, address, items)
- What is the **REST path**? (default: `/api/v1/{entities}`)
- Are there **authorization annotations** to add on the controller? (e.g., `@CanRead{Entities}`)

---

## Phase 3 — Generate the files

Generate all files following the **exact same patterns** found in Phase 1 — same layer names, same package structure, same naming conventions, same filtering strategy, same mapper approach, same `PageModel` location and usage.

The files to generate always cover these responsibilities (adapt names/packages to match the project):

### 1. Filter class
- Fields for each filterable property
- Pagination fields (`page`, `size`)
- Sorting fields (property + direction)
- If project uses `SortablePageFilter<SortType>`: extend it, create inner `SortType` enum with `propertyName` field, override `getSortByPropertyOrDefault()` and `getSortDirectionOrDefault()`
- If project uses a plain filter: add `String sort`, `Sort.Direction direction`, `int page = 0`, `int size = 15` directly on the class

### 2. Output DTOs

**`{Entity}SummaryOutput`** — used in the filter/list endpoint:
- Minimal fields visible in a list view
- If using `builder.construct()` projection: add a positional constructor whose parameter order **exactly matches** the Criteria API `select` call; ID field takes `Long` and converts internally
- If using `Mapper` or static factory: just plain fields with Lombok

**`{Entity}DetailOutput`** — used in the `findById` endpoint:
- All entity fields plus nested relationship DTOs
- Use `@Builder.Default` with `new ArrayList<>()` for collection fields

### 3. Service wiring

Choose based on what was found in Phase 1:

**Option A — Direct service (no ports)**
- `@Service {Entity}QueryService` injected directly into the controller
- Depends on the repository (which must extend `JpaSpecificationExecutor` for Specifications-based filtering)

**Option B — Port-based service (hexagonal)**
- Input port interface: `ForQuerying{Entities}` with `findById` and `filter` methods
- Application service: `{Entity}QueryService implements ForQuerying{Entities}`, delegates to output port
- Output port interface: `{Entity}QueryApplicationService`
- JPA implementation: `{Entity}QueryApplicationServiceJpaImpl implements {Entity}QueryApplicationService`

### 4. Query implementation

Choose based on what was found in Phase 1:

**Option A — Spring Data Specifications**
- Create a `{Entity}Specifications` class with static null-safe `Specification<{Entity}>` factory methods, one per filterable field
- In the service: combine specs with `Specification.where(...).and(...)`, build `PageRequest.of(page, size, Sort.by(direction, sort))`, call `repository.findAll(spec, pageable)`
- Repository must extend `JpaSpecificationExecutor<{Entity}>`

**Option B — Raw Criteria API (EntityManager)**
- Count query first: `CriteriaQuery<Long>` with `builder.count(root)` + `toPredicates()`
- If count = 0 return empty `PageImpl`
- Select query: `builder.construct({Entity}SummaryOutput.class, ...)` projection with nested `builder.construct()` for related entities
- `toPredicates()`: list of null-checked predicates; strings use `builder.upper()` on both sides; date/decimal ranges use `greaterThanOrEqualTo` / `lessThanOrEqualTo`
- `toSortOrder()`: maps filter sort field to `builder.asc()` / `builder.desc()`
- Manual pagination: `setFirstResult(page * size)` + `setMaxResults(size)`
- Return `new PageImpl<>(results, PageRequest.of(...), total)`

### 5. Repository
- Extend `JpaRepository<{Entity}, {IdType}>`
- Add `@EntityGraph(attributePaths = {...})` on `findById` to eager-fetch all paths needed by `{Entity}DetailOutput`
- If using Specifications: also extend `JpaSpecificationExecutor<{Entity}>`

### 6. Controller
- `@RestController @RequestMapping("/api/v1/{entities}")`
- `GET /` → returns `PageModel<{Entity}SummaryOutput>` from `filter({Entity}Filter)`
- `GET /{id}` → returns `{Entity}DetailOutput` from `findById(id)`
- Spring binds filter query params automatically — no `@RequestParam` needed on the filter object
- Add authorization annotations if the project uses them

---

## Phase 4 — Verify

After generating, confirm:

1. `mvn clean compile` passes
2. `GET /api/v1/{entities}` returns `PageModel` with correct fields
3. `GET /api/v1/{entities}?{field}={value}` filters correctly
4. `GET /api/v1/{entities}/{id}` returns the full detail output
5. SQL logs show a `COUNT(*)` before the data query (if using raw Criteria API)
6. No N+1 queries on `findById` (if relationships exist)