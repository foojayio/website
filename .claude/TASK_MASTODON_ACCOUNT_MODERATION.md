# Foojay.social Mastodon Account Moderation Guidelines

Guidelines to help moderate new user registrations on the foojay.social Mastodon instance. Only real people from the OpenJDK community are accepted, and we have to be strict about this to avoid spam accounts.

## Server Rules

As defined on https://foojay.social/auth/sign_up.

1. This Mastodon instance only accepts users from the OpenJDK community. When registering, please add a link to something that can prove your contributions (GitHub, LinkedIn, other social accounts). Sorry, but we get are flooded with spam accounts and can only accept clear requests...
2. We welcome everyone active in the OpenJDK, Java, JavaFX, JVM, and related communities.
3. No harassment, dogpiling, or doxxing of other users.
4. No impersonation of other people, companies, organizations, brands, etc.
5. No racism, sexism, homophobia, transphobia, xenophobia, or casteism.
6. No incitement of violence, or advocacy of violent acts/ideologies, including political or war-related violence.
7. No sexually explicit or violent media.
8. No intentionally false or misleading information.
9. No advertising, spam or excessive promotion.
10. Please do not upload (long) videos here, but use a dedicated service for this goal (Vimeo, PeerTube,...) and post a link in your message.

## Tasks

* Before starting list number of pending and registered users.
* Use the Mastodon API to check PENDING-only registrations and verify their profile information.
* Check the provided description to verify that the user is a real person and part of the OpenJDK community.
* If the user does not provide sufficient proof of their contributions or is found to be a spam account, reject the registration and provide a clear explanation.
* Ask confirmation before accepting a new user.
* At the end again list the number of pending and registered users.
* Never remove any registered user, even if they are later found to be a spam account, just alert about these ones. Only reject pending registrations.

## Recurring Descriptions to Refuse

Match case-insensitively against the account's registration reason text, username, and email combined. Only auto-reject an EXACT match against this list — anything else (even if it looks spammy) goes in the "flag for manual review" bucket below, never auto-rejected.

* Automated protocol deliverability probe
* Deliverability check for the [service].com compatibility matrix (e.g. "Registration-mail deliverability check for the sendflood.com compatibility matrix")

## Setup: Admin API Access

Ask the user for an API token. This is the process to get one as a Mastodon Administrator:

1. Log into foojay.social, go to Preferences → Development (`/settings/applications`), create a new application.
2. Scopes needed: `admin:read:accounts`, `admin:write:accounts` (uncheck all others).
3. Copy the access token. Never paste it into chat/commit it — store it in a local, git-ignored file (e.g. under the session scratchpad) and read it from there.

## API Notes / Gotchas (learned 2026-09-25)

* List pending accounts: `GET /api/v1/admin/accounts?status=pending&origin=local&limit=200`
* Reject one: `POST /api/v1/admin/accounts/:id/reject`
* Approve one: `POST /api/v1/admin/accounts/:id/approve`
* **`status=pending` filter is not reliable once results run out.** This applies even to a *single* request, not just pagination: if you ask for `limit=200` but there are only e.g. 33 genuinely-pending accounts, the response silently backfills the rest of the page with already-approved real accounts sorted by recency, ignoring the filter. Always check `"approved"` on every returned account rather than trusting the filter blindly.
* **Pagination bug:** the response `Link` header's `rel="next"` URL drops the `status=pending`/`origin=local` query params on this instance. Following it verbatim silently pulls in already-approved real accounts (this almost caused real community members, including the site owner's own account, to be rejected). Fix: extract only `max_id` from the `Link` header and rebuild the next request URL yourself with the original filters re-applied.
* **Safety net:** before rejecting anything, verify every account in the batch has `"approved": false`. If an `approved: true` account ever shows up in a "pending" page/response, stop and drop it from the batch — the filter was not honored by the server for that request.
* **Rate limiting:** bulk rejecting many accounts in a row will hit `429 Too many requests`. Add a small delay (~0.3s) between reject calls and retry-with-backoff (respect `Retry-After` header when present) rather than failing outright.
* **Always dry-run first:** print the full match/no-match list before applying any rejection, and get explicit confirmation from Frank on the count before running `--apply`.

## Manual Review Process (for anything not on the exact-match refuse list)

For every remaining pending account, fetch the full admin record (`GET /api/v1/admin/accounts/:id` or the list response's `invite_request`/`account.note`/`account.fields`) and judge against the actual sign-up rule: **a link proving OpenJDK/Java/JVM community contribution (GitHub, LinkedIn, project site, etc.) is required.**

In practice, applied 2026-09-25 on a batch of 33:
* **Reject** anything with no proof link, regardless of how plausible the bio text sounds — this includes bios that explicitly claim to be a Java/Jakarta EE developer but provide zero verifiable link. Two such cases were rejected specifically for this reason.
* **Reject** bios that are generic Fediverse chatter unrelated to Java/OpenJDK (hobbies, other languages, politics, music, etc.) — the large majority of a typical batch.
* Disposable/throwaway email domains are a strong secondary spam signal (seen paired with randomized usernames like `bp<hex>` or `<random letters>`), but not on their own grounds for rejection — a real person could use a privacy-focused provider. Judge on the bio + presence of a proof link first.
* Domains observed in spam signups so far: `onionmail.org`, `emlhub.com`, `pickmail.org`, `attumpt.com`, `maximail.fyi`, `2mail.co`, `10mail.org`, `10mail.xyz`, `x1ix.com`, `spymail.one`, `maillog.uk`, `emlhot.com`, `datadudi.com`, `cltiscy.com`, `raligaan.com`, `trangzim.uk`, `benphim.net`, `freeml.net`, `toan.triennguyen.com`.
* Real, clearly legitimate signups (seen in already-approved history) look like: a specific Java/JVM/JakartaEE/JavaFX project or role stated, often with a GitHub/LinkedIn/personal-site link in `account.fields`, e.g. "JavaChampion and co-organizer of MadridJUG", "Micronaut committer", "creator of JobRunr". These are easy to tell apart from spam once you look past the bio text alone.
* Still confirm with Frank before rejecting anything that isn't an exact refuse-list match or textbook bot nonsense — present the categorized list (clear reject / borderline / looks legitimate) and let him make the call on borderline ones.