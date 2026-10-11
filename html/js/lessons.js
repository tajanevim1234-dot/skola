const SEMESTER_START = new Date(2026, 8, 14);
const SEMESTER_END = new Date(2026, 11, 18, 23, 59, 59, 999);

function getNextLesson(lessons, now) {
    const firstDay = new Date(Math.max(now.getTime(), SEMESTER_START.getTime()));
    firstDay.setHours(0, 0, 0, 0);
    let nextLesson = null;

    for (let offset = 0; offset < 8; offset += 1) {
        const day = new Date(firstDay);
        day.setDate(day.getDate() + offset);

        for (const lesson of lessons) {
            if (Number(lesson.dataset.day) !== day.getDay()) continue;

            const start = new Date(day);
            start.setHours(Number(lesson.dataset.start), 0, 0, 0);

            if (start > now && start >= SEMESTER_START && start <= SEMESTER_END &&
                (!nextLesson || start < nextLesson.start)) {
                nextLesson = { name: lesson.dataset.name, start };
            }
        }
    }

    return nextLesson;
}

function updateSchedule(lessons, now = new Date()) {
    const minutes = now.getHours() * 60 + now.getMinutes();
    const inSemester = now >= SEMESTER_START && now <= SEMESTER_END;
    let currentLesson = null;

    for (const lesson of lessons) {
        const isCurrent = inSemester && Number(lesson.dataset.day) === now.getDay() &&
            minutes >= Number(lesson.dataset.start) * 60 &&
            minutes < Number(lesson.dataset.end) * 60;
        lesson.classList.toggle('is-current', isCurrent);
        if (isCurrent) currentLesson = lesson;
    }

    const status = document.querySelector('#lesson-status');
    if (currentLesson) {
        status.textContent = `Práve mám ${currentLesson.dataset.name} do ${currentLesson.dataset.end}:00.`;
    } else {
        const nextLesson = getNextLesson(lessons, now);
        if (nextLesson) {
            const date = nextLesson.start.toLocaleString('sk-SK', {
                weekday: 'long', day: 'numeric', month: 'numeric',
                hour: '2-digit', minute: '2-digit'
            });
            status.textContent = `Teraz nemám výučbu. Najbližšie mám ${nextLesson.name}: ${date}.`;
        } else {
            status.textContent = 'Teraz nemám výučbu. V tomto semestri už nemám naplánovanú ďalšiu hodinu.';
        }
    }
    status.textContent += ' Čas sa riadi nastavením vášho zariadenia.';

    const elapsed = (now - SEMESTER_START) / (SEMESTER_END - SEMESTER_START);
    const percent = Math.max(0, Math.min(100, Math.floor(elapsed * 100)));
    const progress = document.querySelector('#semester-progress');
    progress.value = percent;
    progress.textContent = `${percent} %`;
    document.querySelector('#semester-label').textContent = `Zo semestra uplynulo ${percent} %.`;
}

const scheduleTable = document.querySelector('.schedule-table');

if (scheduleTable) {
    const lessons = Array.from(scheduleTable.querySelectorAll('.schedule-lesson'));
    const filters = document.querySelectorAll('.filter-button');
    document.querySelector('.schedule-filters').hidden = false;
    document.querySelector('#semester-progress-container').hidden = false;

    for (const button of filters) {
        button.addEventListener('click', () => {
            let visibleCount = 0;

            for (const lesson of lessons) {
                const visible = button.dataset.filter === 'all' ||
                    lesson.dataset.type === button.dataset.filter;
                lesson.classList.toggle('is-filtered', !visible);
                lesson.querySelector('.lesson-content').setAttribute('aria-hidden', String(!visible));
                if (visible) visibleCount += 1;
            }

            for (const filter of filters) {
                filter.setAttribute('aria-pressed', String(filter === button));
            }

            document.querySelector('.schedule-scroll').hidden = visibleCount === 0;
            document.querySelector('#filter-status').textContent = visibleCount === 0
                ? 'Pre tento filter nemám žiadne hodiny.'
                : `Zobrazené hodiny: ${visibleCount}.`;
        });
    }

    updateSchedule(lessons);
    setInterval(() => updateSchedule(lessons), 30000);
}
