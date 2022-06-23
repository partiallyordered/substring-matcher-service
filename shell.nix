# nixpkgs-unstable HEAD at the time of writing
{ pkgs ? import (fetchTarball "https://github.com/NixOS/nixpkgs/archive/103a4c0ae46afa9cf008c30744175315ca38e9f9.tar.gz") {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk17 # See: https://nixos.wiki/wiki/Java
    lnav
  ];
}
