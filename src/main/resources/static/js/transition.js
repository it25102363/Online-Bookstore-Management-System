/*
 * Sign In <-> Sign Up transition
 * ------------------------------------------------------------
 * Clicking a link between /login and /register no longer reloads
 * the page. The new page is fetched, and only the form and the hero
 * text card are swapped, with a slide/fade. The hero image, overlay,
 * sidebar and card frame never change, so they stay perfectly fixed,
 * and the sidebar bar glides to the new icon.
 *
 * Falls back to a normal page load if anything goes wrong.
 */
(() => {
    'use strict';

    const ORDER = { login: 0, register: 1 };
    const EASE = 'cubic-bezier(0.22, 0.7, 0.2, 1)';
    const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    const pageKey = (path) => {
        const m = path.match(/\/(login|register)\/?$/);
        return m ? m[1] : null;
    };

    let current = pageKey(location.pathname);
    let busy = false;

    const $ = (sel, root = document) => root.querySelector(sel);

    // Run a Web Animation. Enter animations are cancelled when done so no
    // inline state is left behind; exit animations are kept until the swap.
    function play(el, frames, options = {}, keep = false) {
        if (reduceMotion || !el) return Promise.resolve();
        const anim = el.animate(frames, { duration: 320, easing: EASE, fill: 'both', ...options });
        return anim.finished.then(() => { if (!keep) anim.cancel(); }, () => {});
    }

    function enter(form, text, dir) {
        return Promise.all([
            play(form, [
                { opacity: 0, transform: `translateX(${28 * dir}px)` },
                { opacity: 1, transform: 'translateX(0)' }
            ], { duration: 380 }),
            play(text, [
                { opacity: 0, transform: 'translateY(12px)' },
                { opacity: 1, transform: 'translateY(0)' }
            ], { duration: 380, delay: 60 })
        ]);
    }

    function exit(form, text, dir) {
        return Promise.all([
            play(form, [
                { opacity: 1, transform: 'translateX(0)' },
                { opacity: 0, transform: `translateX(${-28 * dir}px)` }
            ], { duration: 240 }, true),
            play(text, [
                { opacity: 1, transform: 'translateY(0)' },
                { opacity: 0, transform: 'translateY(-12px)' }
            ], { duration: 240 }, true)
        ]);
    }

    // Copy which sidebar link is active (the bar follows via CSS)
    function syncNav(doc) {
        const mine = document.querySelectorAll('.card-nav a');
        const theirs = doc.querySelectorAll('.card-nav a');
        mine.forEach((a, i) => {
            if (theirs[i]) a.className = theirs[i].className;
        });
    }

    async function go(url, push) {
        if (busy) return;
        const target = new URL(url, location.href);
        const key = pageKey(target.pathname);
        if (!key || key === current) return;

        busy = true;
        let doc;
        try {
            const res = await fetch(target.href, {
                credentials: 'same-origin',
                headers: { 'X-Requested-With': 'fetch' }
            });
            // e.g. already signed in and redirected elsewhere: do a real navigation
            if (!res.ok || pageKey(new URL(res.url).pathname) !== key) throw new Error('unexpected');
            doc = new DOMParser().parseFromString(await res.text(), 'text/html');
            if (!$('.card-form', doc) || !$('.hero-glass-box', doc)) throw new Error('unexpected');
        } catch (err) {
            location.href = target.href;
            return;
        }

        const dir = ORDER[key] > ORDER[current] ? 1 : -1;
        const form = $('.card-form');
        const text = $('.hero-glass-box');

        await exit(form, text, dir);

        const newForm = document.importNode($('.card-form', doc), true);
        const newText = document.importNode($('.hero-glass-box', doc), true);
        form.replaceWith(newForm);
        text.replaceWith(newText);
        syncNav(doc);
        document.title = doc.title;
        if (push) history.pushState({}, '', target.href);
        current = key;

        const firstInput = $('input', newForm);
        if (firstInput) firstInput.focus({ preventScroll: true });

        await enter(newForm, newText, dir);
        busy = false;
    }

    // Intercept clicks on links between the two pages
    document.addEventListener('click', (e) => {
        if (e.defaultPrevented || e.button !== 0 || e.metaKey || e.ctrlKey || e.shiftKey || e.altKey) return;
        const a = e.target.closest('a[href]');
        if (!a || a.target === '_blank' || a.origin !== location.origin) return;
        if ((a.getAttribute('href') || '').startsWith('#')) return;
        const key = pageKey(a.pathname);
        if (!key) return;
        e.preventDefault();
        go(a.href, true);
    });

    // Browser back / forward
    window.addEventListener('popstate', () => go(location.href, false));

    // Soft entrance on a fresh page load (e.g. after logout or registration)
    document.addEventListener('DOMContentLoaded', () => {
        enter($('.card-form'), $('.hero-glass-box'), 1);
    });
})();