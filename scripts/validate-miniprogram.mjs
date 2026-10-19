#!/usr/bin/env node
// Validate app.json routes and local navigation targets for the 3-8 project.
// Usage: node scripts/validate-miniprogram.mjs competition-miniprogram
import fs from "node:fs";
import path from "node:path";

const root = process.argv[2] || "competition-miniprogram";
const app = JSON.parse(fs.readFileSync(path.join(root, "app.json"), "utf8"));
const pages = app.pages || [];
const pageSet = new Set(pages);
const problems = [];

for (const page of pages) {
  for (const ext of [".js", ".wxml"]) {
    if (!fs.existsSync(path.join(root, page + ext))) {
      problems.push(`registered page missing ${page}${ext}`);
    }
  }
}

for (const item of app.tabBar?.list || []) {
  if (!pageSet.has(item.pagePath)) problems.push(`tabBar not registered: ${item.pagePath}`);
}

function walk(dir) {
  const out = [];
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    if (entry.name === "miniprogram_npm") continue;
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) out.push(...walk(full));
    else if (/\.(js|wxml)$/.test(entry.name)) out.push(full);
  }
  return out;
}

const targets = [];
for (const file of walk(path.join(root, "pages"))) {
  const text = fs.readFileSync(file, "utf8");
  const relFile = path.relative(root, file).replaceAll("\\", "/");
  for (const m of text.matchAll(/<navigator[^>]*?url\s*=\s*["']([^"']+)/g)) {
    targets.push([relFile, m[1]]);
  }
  for (const m of text.matchAll(/navigateTo\s*\(\s*\{[\s\S]*?url\s*:\s*["']([^"']+)/g)) {
    targets.push([relFile, m[1]]);
  }
}

for (const [source, rawUrl] of targets) {
  if (/\{\{/.test(rawUrl) || /^https?:\/\//.test(rawUrl)) continue;
  const clean = rawUrl.split("?")[0].split("#")[0];
  const page = clean.startsWith("/")
    ? clean.slice(1)
    : path.posix.normalize(path.posix.join(path.posix.dirname(source), clean));
  if (page.startsWith("pages/") && !pageSet.has(page)) {
    problems.push(`navigation target not registered: ${source} -> ${rawUrl}`);
  }
}

if (problems.length) {
  console.error("Mini-program validation problems:");
  for (const problem of problems) console.error(" -", problem);
  process.exit(1);
}
console.log(`OK: ${pages.length} registered pages, ${targets.length} navigation targets checked.`);
