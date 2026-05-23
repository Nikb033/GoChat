#!/bin/bash
# ============================================================
# GoChat - GitHub Upload Script
# ============================================================
# STEP 1: Create an empty repo named "GoChat" on github.com
# STEP 2: Run this script from inside the GoChat folder
# ============================================================

GITHUB_USERNAME="Nikb033"
REPO_NAME="GoChat"

echo "Initializing git..."
git init
git add .
git commit -m "Initial commit: GoChat Java chat application"
git branch -M main
git remote add origin https://github.com/$GITHUB_USERNAME/$REPO_NAME.git

echo ""
echo "Pushing to GitHub..."
git push -u origin main

echo ""
echo "Done! Visit: https://github.com/$GITHUB_USERNAME/$REPO_NAME"
