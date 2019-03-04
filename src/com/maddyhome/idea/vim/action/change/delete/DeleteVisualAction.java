/*
 * IdeaVim - Vim emulator for IDEs based on the IntelliJ platform
 * Copyright (C) 2003-2019 The IdeaVim authors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.maddyhome.idea.vim.action.change.delete;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.maddyhome.idea.vim.VimPlugin;
import com.maddyhome.idea.vim.action.VimCommandAction;
import com.maddyhome.idea.vim.command.Command;
import com.maddyhome.idea.vim.command.CommandFlags;
import com.maddyhome.idea.vim.command.CommandState;
import com.maddyhome.idea.vim.command.MappingMode;
import com.maddyhome.idea.vim.command.SelectionType;
import com.maddyhome.idea.vim.common.TextRange;
import com.maddyhome.idea.vim.handler.VisualOperatorActionHandler;
import com.maddyhome.idea.vim.helper.EditorHelper;
import com.maddyhome.idea.vim.helper.UtilsKt;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author vlan
 */
public class DeleteVisualAction extends VimCommandAction {
  public DeleteVisualAction() {
    super(new VisualOperatorActionHandler() {
      @Override
      protected boolean executeCharacterAndLinewise(@NotNull Editor editor,
                                                    @NotNull Caret caret,
                                                    @NotNull DataContext context,
                                                    @NotNull Command cmd,
                                                    @NotNull RangeMarker range) {
        final CommandState.SubMode mode = CommandState.getInstance(editor).getSubMode();
        if (mode == CommandState.SubMode.VISUAL_LINE) {
          final TextRange lineRange = new TextRange(EditorHelper.getLineStartForOffset(editor, range.getStartOffset()),
                                                    EditorHelper.getLineEndForOffset(editor, range.getEndOffset()) + 1);
          return VimPlugin.getChange().deleteRange(editor, caret, lineRange, SelectionType.fromSubMode(mode), false);
        }
        else {
          return VimPlugin.getChange()
            .deleteRange(editor, caret, UtilsKt.getVimTextRange(range), SelectionType.fromSubMode(mode), false);
        }
      }

      @Override
      protected boolean executeBlockwise(@NotNull Editor editor,
                                         @NotNull DataContext context,
                                         @NotNull Command cmd,
                                         @NotNull Map<Caret, ? extends RangeMarker> ranges) {
        TextRange vimTextRange = UtilsKt.getVimTextRange(ranges);
        final CommandState.SubMode mode = CommandState.getInstance(editor).getSubMode();
        if (mode == CommandState.SubMode.VISUAL_LINE) {
          final TextRange lineRange =
            new TextRange(EditorHelper.getLineStartForOffset(editor, vimTextRange.getStartOffset()),
                          EditorHelper.getLineEndForOffset(editor, vimTextRange.getEndOffset()) + 1);
          return VimPlugin.getChange()
            .deleteRange(editor, editor.getCaretModel().getPrimaryCaret(), lineRange, SelectionType.fromSubMode(mode),
                         false);
        }
        else {
          return VimPlugin.getChange().deleteRange(editor, editor.getCaretModel().getPrimaryCaret(), vimTextRange,
                                                   SelectionType.fromSubMode(mode), false);
        }
      }
    });
  }

  @NotNull
  @Override
  public Set<MappingMode> getMappingModes() {
    return MappingMode.V;
  }

  @NotNull
  @Override
  public Set<List<KeyStroke>> getKeyStrokesSet() {
    return parseKeysSet("d", "x", "<Del>");
  }

  @NotNull
  @Override
  public Command.Type getType() {
    return Command.Type.DELETE;
  }

  @Override
  public EnumSet<CommandFlags> getFlags() {
    return EnumSet.of(CommandFlags.FLAG_EXIT_VISUAL);
  }
}
